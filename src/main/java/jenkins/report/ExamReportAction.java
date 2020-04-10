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
package jenkins.report;

import hudson.model.Action;
import hudson.model.Run;
import jenkins.model.RunAction2;
import jenkins.task.ExamTask;
import jenkins.task.ExamTaskModel;

import javax.annotation.CheckForNull;

/**
 * The report site of a build with information about the parameterized build.
 *
 * @author Thomas Reinicke
 */
public class ExamReportAction implements Action, RunAction2 {
    
    private transient Run run;
    private ExamTask examTask;
    
    /**
     * Constructor for ExamTool.
     *
     * @param examTask ExamTask
     */
    public ExamReportAction(ExamTask examTask) {
        this.examTask = examTask;
    }
    
    /**
     * Constructor for ExamTool.
     */
    public ExamTask getExamTask() {
        return examTask;
    }
    
    /**
     * returns true, if the Task is a ExamTaskModel
     *
     * @return boolean
     */
    public boolean isModel() {
        return examTask instanceof ExamTaskModel;
    }
    
    /**
     * returns the name of the used model
     *
     * @return String
     */
    public String getExamModel() {
        if (examTask instanceof ExamTaskModel) {
            return ((ExamTaskModel) examTask).getExamModel();
        }
        return "no model configured";
    }
    
    /**
     * returns the value of the used modelConfig
     *
     * @return String
     */
    public String getModelConfiguration() {
        if (examTask instanceof ExamTaskModel) {
            return ((ExamTaskModel) examTask).getModelConfiguration();
        }
        return "no modelConfig configured";
    }
    
    /**
     * returns the value of the testObject
     *
     * @return String
     */
    public String getTestObject() {
        if (examTask instanceof ExamTaskModel) {
            return ((ExamTaskModel) examTask).getExecutionFile();
        }
        return "no test object configured";
    }
    
    /**
     * returns the path to the icon
     *
     * @return String
     */
    @CheckForNull
    @Override
    public String getIconFileName() {
        return "/plugin/exam/images/exam.jpg";
    }
    
    /**
     * returns the value of the name to display
     *
     * @return String
     */
    @CheckForNull
    @Override
    public String getDisplayName() {
        return "EXAM build";
    }
    
    /**
     * returns the name fpr the url
     *
     * @return String
     */
    @CheckForNull
    @Override
    public String getUrlName() {
        return "EXAM_build";
    }
    
    /**
     * @param run Run
     */
    @Override
    public void onAttached(Run<?, ?> run) {
        this.run = run;
    }
    
    /**
     * @param run Run
     */
    @Override
    public void onLoad(Run<?, ?> run) {
        this.run = run;
    }
    
    /**
     * returns the run
     *
     * @return Run
     */
    public Run getRun() {
        return run;
    }
}
