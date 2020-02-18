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
package jenkins.plugins.exam.config;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.internal.DbFactory;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import javax.xml.soap.SOAPException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Global configuration to store all EXAM Models
 *
 * @author Thomas Reinicke
 */
@Extension
public class ExamPluginConfig extends GlobalConfiguration {
    public static final ExamPluginConfig EMPTY_CONFIG = new ExamPluginConfig(Collections.emptyList(),
            Collections.emptyList(), 8085, 5053, 300, "localhost");
    private static final String EXAM_PLUGIN_CONFIGURATION_ID = "exam-plugin-configuration";
    private List<ExamModelConfig> modelConfigs = new ArrayList<>();
    private List<ExamReportConfig> reportConfigs = new ArrayList<>();
    private int port = 8085;
    private int timeout = 300;
    private int licensePort = 0;
    private String licenseHost = "";

    /**
     * Constructor of ExamPluginConfig
     */
    public ExamPluginConfig() {
        load();
    }

    /**
     * Constructor of ExamPluginConfig
     */
    public ExamPluginConfig(List<ExamModelConfig> modelConfigs, List<ExamReportConfig> reportConfigs, int port,
                            int licensePort, int timeout, String licenseHost) {
        this.modelConfigs = modelConfigs;
        this.reportConfigs = reportConfigs;
        this.licenseHost = licenseHost;
        this.licensePort = licensePort;
        this.port = port;
        this.timeout = timeout;
    }

    /**
     * Shortcut method for getting instance of {@link ExamPluginConfig}.
     *
     * @return configuration of plugin
     */
    @Nonnull
    public static ExamPluginConfig configuration() {
        ExamPluginConfig pluginConfig = ExamPluginConfig.all().get(ExamPluginConfig.class);
        return pluginConfig == null ? ExamPluginConfig.EMPTY_CONFIG : pluginConfig;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getLicensePort() {
        return licensePort;
    }

    public void setLicensePort(int licensePort) {
        this.licensePort = licensePort;
    }

    public String getLicenseHost() {
        return licenseHost;
    }

    public void setLicenseHost(String licenseHost) {
        this.licenseHost = licenseHost;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        boolean boolSuper = super.configure(req, json);
        save();
        return boolSuper;
    }

    public List<ExamModelConfig> getModelConfigs() {
        return modelConfigs;
    }

    public void setModelConfigs(List<ExamModelConfig> modelConfigs) {
        this.modelConfigs = modelConfigs;
    }

    public List<ExamReportConfig> getReportConfigs() {
        return reportConfigs;
    }

    public void setReportConfigs(List<ExamReportConfig> reportConfigs) {
        this.reportConfigs = reportConfigs;
    }

    /**
     * To avoid long class name as id in xml tag name and config file
     */
    @Override
    public String getId() {
        return EXAM_PLUGIN_CONFIGURATION_ID;
    }

    @Override
    @Nonnull
    public String getDisplayName() {
        return "EXAM";
    }

    /**
     * Verify every configured Model Connection.
     *
     * @return FormValidation
     * @throws SOAPException
     */
    public FormValidation doVerifyModelConnections() throws SOAPException {

        Map<String, List<String>> status = new HashMap<>();

        for (ExamModelConfig mConfig : modelConfigs) {
            String message = DbFactory.testModelConnection(mConfig.getModelName(), mConfig.getTargetEndpoint(),
                    mConfig.getExamVersion());
            if (!status.containsKey(message)) {
                status.put(message, new ArrayList<>());
            }
            status.get(message).add(mConfig.getName());
        }

        StringBuilder sb = new StringBuilder();

        Set<String> keys = status.keySet();
        if (keys.size() == 1 && keys.contains("OK")) {
            sb.append("connections OK");
            sb.append("\n");
            return FormValidation.ok(sb.toString());
        }

        for (Map.Entry<String, List<String>> entry : status.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("OK")) {
                continue;
            }
            for (String name : entry.getValue()) {
                sb.append(name);
                sb.append(" (");
                sb.append(entry.getKey());
                sb.append(")");
                sb.append("\n");
            }
        }

        return FormValidation.error(sb.toString());
    }
}
