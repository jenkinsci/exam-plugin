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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jenkins.internal.ClientRequest;
import jenkins.internal.enumeration.DbKind;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

@XStreamAlias("exam-report-config") public class ExamReportConfig extends AbstractDescribableImpl<ExamReportConfig> {

    /**
     * The optional display name of this server.
     */
    @CheckForNull private String name = "";
    private String dbType = "";
    private String host = "";
    private String port = "0";
    private String serviceOrSid = "";
    private String schema = "";
    private String dbUser = "";
    private String dbPass = "";

    @DataBoundConstructor public ExamReportConfig() {
    }

    /**
     * Gets the optional display name of this server.
     *
     * @return the optional display name of this server, may be empty or
     * {@code null} but best effort is made to ensure that it has some
     * meaningful text.
     * @since 1.28.0
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the optional display name.
     *
     * @param name the optional display name.
     */
    @DataBoundSetter public void setName(@CheckForNull String name) {
        this.name = Util.fixNull(name).trim();
    }

    public String getDbType() {
        return dbType;
    }

    @DataBoundSetter public void setDbType(@CheckForNull String dbType) {
        this.dbType = Util.fixNull(dbType).trim();
    }

    public String getHost() {
        return host;
    }

    @DataBoundSetter public void setHost(@CheckForNull String host) {
        this.host = Util.fixNull(host).trim();
    }

    public String getPort() {
        return port;
    }

    @DataBoundSetter public void setPort(@CheckForNull String port) {
        this.port = Util.fixNull(port).trim();
    }

    public String getServiceOrSid() {
        return serviceOrSid;
    }

    @DataBoundSetter public void setServiceOrSid(@CheckForNull String serviceOrSid) {
        this.serviceOrSid = Util.fixNull(serviceOrSid).trim();
    }

    public String getSchema() {
        return schema;
    }

    @DataBoundSetter public void setSchema(@CheckForNull String schema) {
        this.schema = Util.fixNull(schema).trim();
    }

    public String getDbUser() {
        return dbUser;
    }

    @DataBoundSetter public void setDbUser(@CheckForNull String dbUser) {
        this.dbUser = Util.fixNull(dbUser).trim();
    }

    public String getDbPass() {
        return dbPass;
    }

    @DataBoundSetter public void setDbPass(@CheckForNull String dbPass) {
        this.dbPass = Util.fixNull(dbPass).trim();
    }

    public String getDisplayName() {
        return Messages.ExamReportConfig_displayName(getName(), getSchema(), getHost(), getPort());
    }

    @Override public ExamReportConfig.DescriptorImpl getDescriptor() {
        Jenkins jenkinsInstance = Jenkins.getInstance();
        if (jenkinsInstance == null) {
            /*
             * Throw AssertionError exception to match behavior of
             * Jenkins.getDescriptorOrDie
             */
            throw new AssertionError("No Jenkins instance");
        }
        return (ExamReportConfig.DescriptorImpl) jenkinsInstance.getDescriptorOrDie(getClass());
    }

    @Extension public static class DescriptorImpl extends Descriptor<ExamReportConfig> {

        private volatile DbKind[] dbTypes = DbKind.values();

        public DescriptorImpl() {
            load();
        }

        protected DescriptorImpl(Class<? extends ExamReportConfig> clazz) {
            super(clazz);
        }

        public DbKind[] getDbTypes() {
            return Arrays.copyOf(dbTypes, dbTypes.length);
        }



        @Override public String getDisplayName() {
            return "EXAM Report";
        }


        public FormValidation doCheckName(@QueryParameter String value) {
            if(value.contains(" ")){
                return FormValidation.error(Messages.ExamPluginConfig_spacesNotAllowed());
            }
            return FormValidation.ok();
        }

        public FormValidation doVerifyConnection(@QueryParameter String name, @QueryParameter String dbType,
                @QueryParameter String host, @QueryParameter String port, @QueryParameter String serviceOrSid,
                @QueryParameter String schema, @QueryParameter String dbUser, @QueryParameter String dbPass)
                throws IOException {

            ExamReportConfig config = new ExamReportConfig();
            config.name = name;
            config.dbType = dbType;
            config.host = host;
            config.port = port;
            config.serviceOrSid = serviceOrSid;
            config.schema = schema;
            config.dbUser = dbUser;
            config.dbPass = dbPass;

            Random rand = new Random();
            int nextInt = rand.nextInt();
            if (nextInt < 0) {
                nextInt *= -1;
            }

            int serverReturned = nextInt % 3;

            switch (serverReturned) {
            case 1: {
                return FormValidation.error("Unknown Name");
            }
            case 2: {
                return FormValidation.error("Unknown Server");
            }
            default: {
                return FormValidation.ok("Conection OK");
            }
            }
        }
    }
}
