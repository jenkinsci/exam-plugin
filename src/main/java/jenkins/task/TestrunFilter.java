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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.io.Serializable;

@XStreamAlias("testrun-filter")
public class TestrunFilter extends AbstractDescribableImpl<TestrunFilter> implements Serializable {
    
    private static final long serialVersionUID = 1198627925572702398L;
    @CheckForNull
    protected String name;
    @CheckForNull
    protected String value;
    protected boolean adminCases;
    protected boolean activateTestcases = false;
    
    /**
     * Constructor of the TestrunFilter Part
     *
     * @param name              attribute name
     * @param value             regex
     * @param adminCases        check admincases
     * @param activateTestcases activate testcases
     */
    @DataBoundConstructor
    public TestrunFilter(String name, String value, boolean adminCases, boolean activateTestcases) {
        this.name = name;
        this.value = value;
        this.adminCases = adminCases;
        this.activateTestcases = activateTestcases;
    }
    
    /**
     * returns the attribute name
     *
     * @return String
     */
    public String getName() {
        return name;
    }
    
    /**
     * returns the attribute value
     *
     * @return reqex String
     */
    public String getValue() {
        return value;
    }
    
    /**
     * returns true is adminCase is set
     *
     * @return boolean
     */
    public boolean getAdminCases() {
        return adminCases;
    }
    
    /**
     * returns true is activate testCases is set
     *
     * @return boolean
     */
    public boolean getActivateTestcases() {
        return activateTestcases;
    }
    
    /**
     * returns the descriptor
     *
     * @return DescriptorImpl
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }
    
    /**
     * Descriptor of the TestrunFilter Part
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<TestrunFilter> {
        
        /**
         * Descriptor of the TestrunFilter Part
         */
        public DescriptorImpl() {
            load();
        }
        
        protected DescriptorImpl(Class<? extends TestrunFilter> clazz) {
            super(clazz);
            load();
        }
        
        @Nonnull
        @Override
        public String getDisplayName() {
            return "Testrun Filter";
        }
        
    }
}
