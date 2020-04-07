package jenkins.task;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Node;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import jenkins.internal.Remote;
import jenkins.internal.Util;
import jenkins.internal.data.TestConfiguration;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task._exam.Messages;
import org.apache.commons.lang.RandomStringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExamTaskHelper {

    private Run run;
    private EnvVars env;
    private Launcher launcher;
    private FilePath workspace;
    private TaskListener listener;

    public ExamTaskHelper(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
            @Nonnull TaskListener listener) throws IOException, InterruptedException {
        this.run = run;
        this.env = run.getEnvironment(listener);
        this.launcher = launcher;
        this.workspace = workspace;
        this.listener = listener;
    }

    public EnvVars getEnv() {
        return env;
    }

    public TaskListener getListener() {
        return listener;
    }

    /**
     * Resolves the given Python to the path on the target node
     *
     * @param python
     * @return String
     * @throws IOException
     * @throws InterruptedException
     */
    public String getPythonExePath(PythonInstallation python) throws IOException, InterruptedException {
        PythonInstallation pythonNode = python.forNode(getNode(), listener);
        String pythonExe = pythonNode.getHome();
        if (pythonExe == null || pythonExe.trim().isEmpty()) {
            run.setResult(Result.FAILURE);
            throw new AbortException("python home not set");
        }
        if (!pythonExe.endsWith("exe")) {
            if (!pythonExe.endsWith("\\") && !pythonExe.endsWith("/")) {
                pythonExe += File.separator;
            }
            pythonExe += "python.exe";
        }
        return pythonExe;
    }

    public ArgumentListBuilder handleAdditionalArgs(String javaOpts, ArgumentListBuilder args, ExamPluginConfig examPluginConfig) throws AbortException {
        ArgumentListBuilder argsNew = args;

        if (examPluginConfig.getLicenseHost().isEmpty() || examPluginConfig.getLicensePort() == 0) {
            run.setResult(Result.FAILURE);
            throw new AbortException(
                    Messages.EXAM_LicenseServerNotConfigured());
        }
        argsNew.add("--launcher.appendVmargs", "-vmargs", "-DUSE_CONSOLE=true",
                "-DRESTAPI=true",
                "-DRESTAPI_PORT=" + examPluginConfig.getPort());

        argsNew.add("-DLICENSE_PORT=" + examPluginConfig.getLicensePort(),
                "-DLICENSE_HOST=" + examPluginConfig.getLicenseHost());

        argsNew.add("-Dfile.encoding=UTF-8");
        argsNew.add("-Dsun.jnu.encoding=UTF-8");

        if (javaOpts != null) {
            env.put("JAVA_OPTS", env.expand(javaOpts));
            String[] splittedJavaOpts = javaOpts.split(" ");
            argsNew.add(splittedJavaOpts);
        }

        if (!launcher.isUnix()) {
            argsNew = toWindowsCommand(argsNew);
        }

        return argsNew;
    }


    /**
     * Backward compatibility by checking the number of parameters
     */
    private static ArgumentListBuilder toWindowsCommand(ArgumentListBuilder args) {
        List<String> arguments = args.toList();

        // branch for core equals or greater than 1.654
        boolean[] masks = args.toMaskArray();
        // don't know why are missing single quotes.

        ArgumentListBuilder argsNew = new ArgumentListBuilder();
        argsNew.add(arguments.get(0), arguments.get(1)); // "cmd.exe", "/C",
        // ...

        int size = arguments.size();
        for (int i = 2; i < size; i++) {
            String arg = arguments.get(i).replaceAll("^(-D[^\" ]+)=$",
                    "$0\"\"");

            if (masks[i]) {
                argsNew.addMasked(arg);
            } else {
                argsNew.add(arg);
            }
        }

        return argsNew;
    }

    /**
     * copies all reports from the EXAM workspace to the target folder
     *
     * @param tc
     * @throws IOException
     * @throws InterruptedException
     */
    public void copyArtifactsToTarget(TestConfiguration tc) throws IOException, InterruptedException {
        FilePath source = workspace.child("workspace_exam_restApi");
        FilePath target = workspace.child("target");
        String hash = "__" + RandomStringUtils.random(5,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray());
        source = source.child("reports").child(
                tc.getReportProject().getProjectName()).child("junit");
        target = target.child("test-reports").child(
                tc.getReportProject().getProjectName() + hash);
        source.copyRecursiveTo(target);
    }

    public String getConfigurationPath(ExamTool examTool) throws IOException, InterruptedException {
        String dataPath = examTool.getHome();
        String relativeDataPath = examTool.getRelativeDataPath();
        if (relativeDataPath != null && !relativeDataPath.trim().isEmpty()) {
            dataPath = examTool.getHome() + File.separator + relativeDataPath;
        }

        String configurationPath = dataPath + File.separator + "configuration";
        File configurationFile = new File(
                dataPath + File.separator + "configuration" + File.separator + "config.ini");
        if (!Remote.fileExists(launcher, configurationFile)) {
            run.setResult(Result.FAILURE);
            throw new AbortException(Messages.EXAM_NotExamConfigDirectory(
                    configurationFile.getPath()));
        }
        return configurationPath;
    }

    public Node getNode() throws AbortException {
        Node node = Util.workspaceToNode(workspace);
        if (node == null) {
            run.setResult(Result.FAILURE);
            throw new AbortException(Messages.EXAM_NodeOffline());
        }
        return node;
    }

    public FilePath prepareWorkspace(ExamTool examTool, ArgumentListBuilder args) throws IOException, InterruptedException {
        String exe = examTool.getExecutable(launcher);
        assert exe != null;
        if (exe.trim().isEmpty()) {
            run.setResult(Result.FAILURE);
            throw new AbortException(Messages.EXAM_ExecutableNotFound(examTool.getName()));
        }

        args.add(exe);

        FilePath buildFilePath = new FilePath(new File(exe));

        String configurationPath = getConfigurationPath(examTool);
        String examWorkspace = workspace + File.separator + "workspace_exam_restApi";
        examWorkspace = examWorkspace.replaceAll("[/\\]]", File.separator);

        args.add("-data", examWorkspace);
        args.add("-configuration", configurationPath);
        examTool.buildEnvVars(env);
        return buildFilePath;
    }

    public ExamTool getTool(@Nullable ExamTool tool) throws IOException, InterruptedException {
        Node node = getNode();
        // check for installations
        if (tool == null) {
            run.setResult(Result.FAILURE);
            throw new AbortException("examTool is null");
        } else {
            tool = tool.forNode(node, listener);
        }
        return tool;
    }

    public void handleIOException(long startTime, IOException e, ExamTool[] installations) throws AbortException {
        hudson.Util.displayIOException(e, listener);

        String errorMessage = Messages.EXAM_ExecFailed();
        long current = System.currentTimeMillis();
        if ((current - startTime) < 1000) {

            if (installations.length == 0)
            // looks like the user didn't configure any EXAM
            // installation
            {
                errorMessage += Messages.EXAM_GlobalConfigNeeded();
            } else
            // There are EXAM installations configured but the project
            // didn't pick it
            {
                errorMessage += Messages.EXAM_ProjectConfigNeeded();
            }
        }
        run.setResult(Result.FAILURE);
        throw new AbortException(errorMessage);
    }
}
