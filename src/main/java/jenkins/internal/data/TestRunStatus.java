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
 * Status of running Testrun at EXAM
 *
 * @author koblofsky
 */
public class TestRunStatus implements Serializable {
    /**
     * Properties
     */
    private String tcName;
    private String tcFullScopedName;
    private int testCaseCount;
    private int currentTestCaseIdx;
    private String expectedRuntime;
    private String currentRuntime;
    private String remainingRuntime;
    
    /**
     * Gets the name of the currently running Testcase.
     *
     * @return the name.
     */
    public String getName() {
        return this.tcName;
    }
    
    /**
     * Sets the name of the current Testcase.
     *
     * @param name the name.
     */
    public void setName(String name) {
        this.tcName = name;
    }
    
    /**
     * Gets the fullscopedname of the currently running Testcase.
     *
     * @return the fullscopedname.
     */
    public String getFullScopedName() {
        return this.tcFullScopedName;
    }
    
    /**
     * Sets the fullscopedname of the current Testcase.
     *
     * @param fullScopedName the fullscopedname.
     */
    public void setFullScopedName(String fullScopedName) {
        this.tcFullScopedName = fullScopedName;
    }
    
    /**
     * Gets the testCaseCount.
     *
     * @return the testCaseCount.
     */
    public int getTestCaseCount() {
        return this.testCaseCount;
    }
    
    /**
     * Sets the testCaseCount.
     *
     * @param testCaseCount the testCaseCount.
     */
    public void setTestCaseCount(int testCaseCount) {
        this.testCaseCount = testCaseCount;
    }
    
    /**
     * Gets the current Testcase index.
     *
     * @return the index.
     */
    public int getCurrentTestCaseIdx() {
        return this.currentTestCaseIdx;
    }
    
    /**
     * Sets the current Testcase index.
     *
     * @param currentTestCaseIdx the index.
     */
    public void setCurrentTestCaseIdx(int currentTestCaseIdx) {
        this.currentTestCaseIdx = currentTestCaseIdx;
    }
    
    /**
     * Gets the expected Runtime as String.
     *
     * @return the expected Runtime.
     */
    public String getExpectedRuntime() {
        return this.expectedRuntime;
    }
    
    /**
     * Sets the expected Runtime.
     *
     * @param expectedRuntime the expected Runtime.
     */
    public void setExpectedRuntime(String expectedRuntime) {
        this.expectedRuntime = expectedRuntime;
    }
    
    /**
     * Gets the current Runtime.
     *
     * @return the current Runtime.
     */
    public String getCurrentRuntime() {
        return this.currentRuntime;
    }
    
    /**
     * Sets the current Runtime.
     *
     * @param currentRuntime the current Runtime.
     */
    public void setCurrentRuntime(String currentRuntime) {
        this.currentRuntime = currentRuntime;
    }
    
    /**
     * Gets the remaining Runtime.
     *
     * @return the remaining Runtime.
     */
    public String getRemainingRuntime() {
        return this.remainingRuntime;
    }
    
    /**
     * Sets the remaining Runtime.
     *
     * @param remainingRuntime the remaining Runtime.
     */
    public void setRemainingRuntime(String remainingRuntime) {
        this.remainingRuntime = remainingRuntime;
    }
}
