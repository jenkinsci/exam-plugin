/**
 * Copyright (c) 2018 MicroNova AG
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * <p>
 * 3. Neither the name of MicroNova AG nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jenkins.task;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.Executor;
import hudson.model.Node;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import jenkins.internal.ClientRequest;
import jenkins.internal.Remote;
import jenkins.internal.Util;
import jenkins.internal.data.ApiVersion;
import jenkins.internal.data.TestConfiguration;
import jenkins.model.Jenkins;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamPluginConfig;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.task._exam.ExamConsoleAnnotator;
import jenkins.task._exam.ExamConsoleErrorOut;
import jenkins.task._exam.Messages;
import org.apache.commons.lang.RandomStringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for all EXAM Tasks.
 *
 * @author Thomas Reinicke
 */
public class ExamTaskHelper {
    
    private Run run;
    private EnvVars env;
    private Launcher launcher;
    private FilePath workspace;
    private TaskListener taskListener;
    
    /**
     * Constructor for ExamTaskHelper.
     *
     * @param run          Run
     * @param workspace    FilePath
     * @param launcher     Launcher
     * @param taskListener TaskListener
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
    
    public ExamTaskHelper(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace, @Nonnull Launcher launcher,
            @Nonnull TaskListener taskListener) throws IOException, InterruptedException {
        this.run = run;
        this.env = run.getEnvironment(taskListener);
        this.launcher = launcher;
        this.workspace = workspace;
        this.taskListener = taskListener;
    }
    
    /**
     * get the environment variables
     *
     * @return EnvVars
     */
    public EnvVars getEnv() {
        return env;
    }
    
    /**
     * get the Run
     *
     * @return Run
     */
    public Run getRun() {
        return run;
    }
    
    /**
     * get the TaskListener
     *
     * @return TaskListener
     */
    public TaskListener getTaskListener() {
        return taskListener;
    }
    
    /**
     * Resolves the given Python to the path on the target node
     *
     * @param python
     *
     * @return String
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public String getPythonExePath(PythonInstallation python) throws IOException, InterruptedException {
        PythonInstallation pythonNode = python.forNode(getNode(), taskListener);
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
    
    /**
     * handle and extend the run arguments
     *
     * @param args             run arguments
     * @param examPluginConfig ExamPluginConfig
     * @param javaOpts         additional java options
     *
     * @return ArgumentListBuilder
     */
    public ArgumentListBuilder handleAdditionalArgs(String javaOpts, ArgumentListBuilder args,
            ExamPluginConfig examPluginConfig) throws AbortException {
        ArgumentListBuilder argsNew = args;
        
        if (examPluginConfig.getLicenseHost().isEmpty() || examPluginConfig.getLicensePort() == 0) {
            run.setResult(Result.FAILURE);
            throw new AbortException(Messages.EXAM_LicenseServerNotConfigured());
        }
        argsNew.add("--launcher.appendVmargs", "-vmargs", "-DUSE_CONSOLE=true", "-DRESTAPI=true",
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
            String arg = arguments.get(i).replaceAll("^(-D[^\" ]+)=$", "$0\"\"");
            
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
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void copyArtifactsToTarget(TestConfiguration tc) throws IOException, InterruptedException {
        FilePath source = workspace.child("workspace_exam_restApi");
        FilePath target = workspace.child("target");
        String hash = "__" + RandomStringUtils.random(5, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray());
        source = source.child("reports").child(tc.getReportProject().getProjectName()).child("junit");
        target = target.child("test-reports").child(tc.getReportProject().getProjectName() + hash);
        source.copyRecursiveTo(target);
    }
    
    /**
     * Calculate and return the path to the configuration folder of EXAM
     *
     * @param examTool
     *
     * @return
     *
     * @throws IOException          IOException
     * @throws InterruptedException InterruptedException
     */
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
            throw new AbortException(Messages.EXAM_NotExamConfigDirectory(configurationFile.getPath()));
        }
        return configurationPath;
    }
    
    /**
     * returns the node the job is actual running
     *
     * @return
     *
     * @throws AbortException AbortException
     */
    public Node getNode() throws AbortException {
        Node node = Util.workspaceToNode(workspace);
        if (node == null) {
            run.setResult(Result.FAILURE);
            throw new AbortException(Messages.EXAM_NodeOffline());
        }
        return node;
    }
    
    /**
     * prepare some configurations and arguments to run EXAM
     *
     * @param examTool
     * @param args
     *
     * @return the path to EXAM runnable
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public FilePath prepareWorkspace(ExamTool examTool, ArgumentListBuilder args)
            throws IOException, InterruptedException {
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
    
    /**
     * returns the tool for EXAM on the actual running node
     *
     * @param tool
     *
     * @return
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public ExamTool getTool(@Nullable ExamTool tool) throws IOException, InterruptedException {
        Node node = getNode();
        // check for installations
        if (tool == null) {
            run.setResult(Result.FAILURE);
            throw new AbortException("examTool is null");
        } else {
            tool = tool.forNode(node, taskListener);
        }
        return tool;
    }
    
    /**
     * handles and logs the IOExcetion within the EXAM task
     *
     * @param startTime     long
     * @param e             IOException
     * @param installations ExamTool[]
     *
     * @throws AbortException AbortException
     */
    public void handleIOException(long startTime, IOException e, ExamTool[] installations) throws AbortException {
        hudson.Util.displayIOException(e, taskListener);
        
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
    
    private void disconnectAndCloseEXAM(@Nonnull ClientRequest clientRequest, Proc proc, int timeout)
            throws IOException, InterruptedException {
        Executor runExecutor = run.getExecutor();
        
        try {
            clientRequest.disconnectClient(runExecutor, timeout);
        } finally {
            if (proc != null && proc.isAlive()) {
                proc.joinWithTimeout(10, TimeUnit.SECONDS, taskListener);
            }
        }
    }
    
    void perform(@Nonnull Task task, @Nonnull Launcher launcher, ApiVersion minApiVersion)
            throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        ExamTool examTool = getTool(task.getExam());
        
        FilePath buildFilePath = prepareWorkspace(examTool, args);
        
        Jenkins instanceOrNull = Jenkins.getInstanceOrNull();
        assert instanceOrNull != null;
        ExamPluginConfig examPluginConfig = instanceOrNull.getDescriptorByType(ExamPluginConfig.class);
        args = handleAdditionalArgs(task.javaOpts, args, examPluginConfig);
        
        long startTime = System.currentTimeMillis();
        try {
            ClientRequest clientRequest = new ClientRequest(taskListener.getLogger(), examPluginConfig.getPort(),
                    launcher);
            if (clientRequest.isApiAvailable()) {
                taskListener.getLogger().println("ERROR: EXAM is already running");
                failTask("ERROR: EXAM is already running");
            }
            Proc proc = null;
            try (ExamConsoleAnnotator eca = new ExamConsoleAnnotator(taskListener.getLogger(), run.getCharset());
                    ExamConsoleErrorOut examErr = new ExamConsoleErrorOut(taskListener.getLogger())) {
                jenkins.internal.Util.checkMinRestApiVersion(taskListener, minApiVersion, clientRequest);
                Launcher.ProcStarter process = launcher.launch().cmds(args).envs(getEnv())
                        .pwd(buildFilePath.getParent());
                process.stderr(examErr).stdout(eca);
                proc = process.start();
                
                task.doExecuteTask(clientRequest);
            } catch (IOException e) {
                failTask("ERROR: " + e.toString());
            } finally {
                disconnectAndCloseEXAM(clientRequest, proc, task.timeout);
            }
            run.setResult(Result.SUCCESS);
        } catch (IOException e) {
            handleIOException(startTime, e, task.getDescriptor().getInstallations());
        } finally {
            printResult();
        }
    }
    
    private void printResult() {
        Result result = run.getResult();
        if (result != null) {
            taskListener.getLogger().println(result.toString());
        }
    }
    
    private void failTask(String exceptionMessage) throws AbortException {
        run.setResult(Result.FAILURE);
        throw new AbortException(exceptionMessage);
    }
}
