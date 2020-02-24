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
import jenkins.internal.data.TestConfiguration;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task._exam.Messages;
import org.apache.commons.lang.RandomStringUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExamTaskHelper {

    Run run;
    EnvVars env;

    public ExamTaskHelper(@Nonnull Run<?, ?> run, @Nonnull EnvVars env) {
        this.run = run;
        this.env = env;
    }

    /**
     * Resolves the given Python to the path on the target node
     *
     * @param listener
     * @param python
     * @param node
     * @return String
     * @throws IOException
     * @throws InterruptedException
     */
    public String getPythonExePath(@Nonnull TaskListener listener, PythonInstallation python, Node node) throws IOException, InterruptedException {
        PythonInstallation pythonNode = python.forNode(node, listener);
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

    public ArgumentListBuilder handleAdditionalArgs(String javaOpts, ArgumentListBuilder args, ExamPluginConfig examPluginConfig, Launcher launcher) throws AbortException {
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
     * @param workspace
     * @param tc
     * @throws IOException
     * @throws InterruptedException
     */
    public void copyArtifactsToTarget(@Nonnull FilePath workspace, TestConfiguration tc) throws IOException, InterruptedException {
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

    public String getConfigurationPath(@Nonnull Launcher launcher, ExamTool examTool) throws IOException, InterruptedException {
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
}
