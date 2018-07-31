/**
 * Copyright (c) 2018 MicroNova AG
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *        list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this
 *        list of conditions and the following disclaimer in the documentation and/or
 *        other materials provided with the distribution.
 *
 *     3. Neither the name of MicroNova AG nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
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
package jenkins.plugins.exam;

import hudson.*;
import hudson.model.EnvironmentSpecific;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.slaves.NodeSpecific;
import hudson.tools.ToolDescriptor;
import hudson.tools.ToolInstallation;
import hudson.tools.ToolProperty;
import jenkins.model.Jenkins;
import jenkins.security.MasterToSlaveCallable;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Information about EXAM installation. A ExamTool is used to select between
 * different installations of EXAM.
 *
 * @author Thomas Reinicke
 */
public class ExamTool extends ToolInstallation implements NodeSpecific<ExamTool>, EnvironmentSpecific<ExamTool> {

    @SuppressWarnings("unused") private static final Logger LOGGER = Logger.getLogger(ExamTool.class.getName());

    private String relativeDataPath;

    /**
     * Constructor for ExamTool.
     *
     * @param name       Tool name (for example, "exam")
     * @param home       Tool location (usually "c:\program files\EXAM\exam.exe")
     * @param properties {@link java.util.List} of properties for this tool
     */
    @DataBoundConstructor public ExamTool(String name, String home, String relativeConfigPath,
            List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
        this.relativeDataPath = Util.fixEmptyAndTrim(relativeConfigPath);
    }

    public ExamTool(String name, String home, List<? extends ToolProperty<?>> properties) {
        super(name, home, properties);
        this.relativeDataPath = null;
    }

    @DataBoundSetter
    public void setRelativeDataPath(String relativeDataPath) {
        this.relativeDataPath = relativeDataPath;
    }

    public String getRelativeConfigPath() {
        return relativeDataPath;
    }

    /**
     * Constant <code>DEFAULT="Default"</code>
     */
    public static transient final String DEFAULT = "Default";

    private static final long serialVersionUID = 1;

    public ExamTool forNode(Node node, TaskListener log) throws IOException, InterruptedException {
        return new ExamTool(getName(), translateFor(node, log), getRelativeConfigPath(),
                Collections.<ToolProperty<?>>emptyList());
    }

    @Override public ExamTool forEnvironment(EnvVars environment) {
        return new ExamTool(getName(), environment.expand(getHome()), getRelativeConfigPath(),
                Collections.<ToolProperty<?>>emptyList());
    }

    @Override public DescriptorImpl getDescriptor() {
        Jenkins jenkinsInstance = Jenkins.getInstance();
        if (jenkinsInstance == null) {
            /*
             * Throw AssertionError exception to match behavior of
             * Jenkins.getDescriptorOrDie
             */
            throw new AssertionError("No Jenkins instance");
        }
        return (DescriptorImpl) jenkinsInstance.getDescriptorOrDie(getClass());
    }

    @Extension
    @Symbol("ExamTool")
    public static class DescriptorImpl extends ToolDescriptor<ExamTool> {
        @CopyOnWrite private volatile ExamTool[] installations = new ExamTool[0];

        public DescriptorImpl() {
            super();
            load();
        }

        @Override public String getDisplayName() {
            return "EXAM";
        }

        @Override public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            installations = req.bindJSONToList(clazz, json.get("tool")).toArray(new ExamTool[0]);
            req.bindJSON(this, json);
            save();
            return true;
        }

        // for compatibility reasons, the persistence is done by
        // Exam.DescriptorImpl
        @Override public ExamTool[] getInstallations() {
            return installations;
        }

        @Override public void setInstallations(ExamTool... installations) {
            this.installations = installations;
        }

        /**
         * Return list of applicable GitTool descriptors.
         * @return list of applicable GitTool descriptors
         */
        @SuppressWarnings("unchecked")
        public List<ToolDescriptor<? extends ExamTool>> getApplicableDescriptors() {
            List<ToolDescriptor<? extends ExamTool>> r = new ArrayList<>();
            Jenkins jenkinsInstance = Jenkins.getInstance();
            for (ToolDescriptor<?> td : jenkinsInstance.<ToolInstallation,ToolDescriptor<?>>getDescriptorList(ToolInstallation.class)) {
                if (ExamTool.class.isAssignableFrom(td.clazz)) { // This checks cast is allowed
                    r.add((ToolDescriptor<? extends ExamTool>)td); // This is the unchecked cast
                }
            }
            return r;
        }
    }

    /**
     * Gets the executable path of this EXAM on the given target system.
     */
    public String getExecutable(Launcher launcher) throws IOException, InterruptedException {
        return launcher.getChannel().call(new MasterToSlaveCallable<String, IOException>() {
            private static final long serialVersionUID = 906341330603832653L;

            public String call() throws IOException {
                File exe = getExeFile();
                if (exe.exists())
                    return exe.getPath();
                return null;
            }
        });
    }

    private File getExeFile() {
        String execName = "EXAM.exe";
        String home = Util.replaceMacro(getHome(), EnvVars.masterEnvVars);

        return new File(home, execName);
    }

}
