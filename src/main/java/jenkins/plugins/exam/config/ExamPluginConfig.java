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
package jenkins.plugins.exam.config;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.internal.DbFactory;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.xml.soap.SOAPException;
import java.util.*;

/**
 * Global configuration to store all EXAM Models
 *
 * @author Thomas Reinicke
 */
@Extension public class ExamPluginConfig extends GlobalConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExamPluginConfig.class);
    public static final String EXAM_PLUGIN_CONFIGURATION_ID = "exam-plugin-configuration";

    public static final ExamPluginConfig EMPTY_CONFIG = new ExamPluginConfig(Collections.<ExamModelConfig>emptyList(),
            Collections.<ExamReportConfig>emptyList(), 8085);

    private List<ExamModelConfig> modelConfigs = new ArrayList<ExamModelConfig>();
    private List<ExamReportConfig> reportConfigs = new ArrayList<ExamReportConfig>();
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ExamPluginConfig() {
        load();
    }

    @Override public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        boolean bsuper = super.configure(req, json);
        save();
        return bsuper && true;
    }

    public ExamPluginConfig(List<ExamModelConfig> modelConfigs, List<ExamReportConfig> reportConfigs, int port) {
        this.modelConfigs = modelConfigs;
        this.reportConfigs = reportConfigs;
        this.port = port;
    }

    public void setModelConfigs(List<ExamModelConfig> modelConfigs) {
        this.modelConfigs = modelConfigs;
    }

    public List<ExamModelConfig> getModelConfigs() {
        return modelConfigs;
    }

    public void setReportConfigs(List<ExamReportConfig> reportConfigs) {
        this.reportConfigs = reportConfigs;
    }

    public List<ExamReportConfig> getReportConfigs() {
        return reportConfigs;
    }

    /**
     * To avoid long class name as id in xml tag name and config file
     */
    @Override public String getId() {
        return EXAM_PLUGIN_CONFIGURATION_ID;
    }

    @Override public String getDisplayName() {
        return "EXAM";
    }

    /**
     * Shortcut method for getting instance of {@link ExamPluginConfig}.
     *
     * @return configuration of plugin
     */
    @Nonnull public static ExamPluginConfig configuration() {
        if (ExamPluginConfig.all().get(ExamPluginConfig.class) == null) {
            return ExamPluginConfig.EMPTY_CONFIG;
        }
        return ExamPluginConfig.all().get(ExamPluginConfig.class);
    }



    public FormValidation doVerifyModelConnections()
            throws SOAPException {

        Map<String, List<String>> status = new HashMap<>();

        for(ExamModelConfig mConfig: modelConfigs) {
            String message = DbFactory.testModelConnection(mConfig.getModelName(), mConfig.getTargetEndpoint(),
                    mConfig.getExamVersion());
            if(!status.containsKey(message)){
                status.put(message, new ArrayList<String>());
            }
            status.get(message).add(mConfig.getName());
        }

        StringBuilder sb = new StringBuilder();

        Set<String> keys = status.keySet();
        if(keys.size() == 1 && keys.contains("OK")){
            sb.append("conections OK");
            sb.append("\n");
            return FormValidation.ok(sb.toString());
        }

        for(Map.Entry<String, List<String>> entry : status.entrySet()){
            if(entry.getKey().equalsIgnoreCase("OK")){
                continue;
            }
            for (String name : entry.getValue()){
                sb.append(name);
                sb.append(" (");
                sb.append(entry.getKey());
                sb.append(")");
                sb.append("\n");
            }
        }

        return FormValidation.error(sb.toString());
    }

    public FormValidation doCheckModelConfigs(@QueryParameter ExamModelConfig configs){
        return FormValidation.error("HALLO");
    }
}
