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
 * Status of running Jobs at EXAM
 *
 * @author liu
 */
public class ExamStatus implements Serializable {
    
    private static final long serialVersionUID = 8611966286168032281L;
    private Boolean jobRunning;
    private String jobName;
    private Integer testRunState;
    private TestRunStatus status;

    /**
     * @return Das jobRunning.
     */
    public Boolean getJobRunning() {
        return this.jobRunning;
    }
    
    /**
     * Setzt das jobRunning.
     *
     * @param jobRunning Das zu setzende jobRunning.
     */
    public void setJobRunning(Boolean jobRunning) {
        this.jobRunning = jobRunning;
    }
    
    /**
     * @return Das jobName.
     */
    public String getJobName() {
        return this.jobName;
    }
    
    /**
     * Setzt das jobName.
     *
     * @param jobName Das zu setzende jobName.
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    
    /**
     * @return Das testRunState.
     */
    public Integer getTestRunState() {
        return this.testRunState;
    }
    
    /**
     * Setzt das testRunState.
     *
     * @param testRunState Das zu setzende testRunState.
     */
    public void setTestRunState(Integer testRunState) {
        this.testRunState = testRunState;
    }

    /**
     * @return der {@link TestRunStatus}.
     */
    public TestRunStatus getStatus() {
        return this.status;
    }

    /**
     * Setzt den {@link TestRunStatus}.
     * @param status
     *            der Status.
     */
    public void setStatus(TestRunStatus status) {
        this.status = status;
    }
}
