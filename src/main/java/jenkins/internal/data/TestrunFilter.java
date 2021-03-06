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
package jenkins.internal.data;

import java.io.Serializable;

/**
 * EXAM Testrunfilter
 */
public class TestrunFilter implements Serializable {
    
    private static final long serialVersionUID = 1999022538751995266L;
    protected String name;
    protected String value;
    protected Boolean adminCases = Boolean.FALSE;
    protected Boolean activateTestcases = Boolean.FALSE;
    
    /**
     * Constructor of an EXAM Testrunfilter
     */
    public TestrunFilter() {
    }
    
    /**
     * Constructor of an EXAM Testrunfilter
     *
     * @param name              attribute name
     * @param value             regex
     * @param adminCases        check admincases
     * @param activateTestcases activate testcases
     */
    public TestrunFilter(String name, String value, Boolean adminCases, Boolean activateTestcases) {
        this.name = name;
        this.value = value;
        this.adminCases = adminCases;
        this.activateTestcases = activateTestcases;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public Boolean isAdminCases() {
        return this.adminCases;
    }
    
    public void setAdminCases(Boolean adminCases) {
        this.adminCases = adminCases;
    }
    
    public Boolean isActivateTestcases() {
        return this.activateTestcases;
    }
    
    public void setActivateTestcases(Boolean activateTestcases) {
        this.activateTestcases = activateTestcases;
    }
}
