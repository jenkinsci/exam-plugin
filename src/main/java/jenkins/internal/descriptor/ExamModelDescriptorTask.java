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
package jenkins.internal.descriptor;

import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.internal.Util;
import jenkins.plugins.exam.ExamTool;
import jenkins.plugins.exam.config.ExamModelConfig;
import jenkins.task.Task;
import org.kohsuke.stapler.QueryParameter;

import java.util.Arrays;
import java.util.List;

/**
 * Descriptor for Tasks that need a EXAM Model.
 */
public class ExamModelDescriptorTask extends Task.DescriptorTask {

    /**
     * fills the ListBoxModel with all ExamInstallations
     *
     * @return ListBoxModel
     */
    public ListBoxModel doFillExamNameItems() {
        ListBoxModel items = new ListBoxModel();
        ExamTool[] examTools = getInstallations();

        Arrays.sort(examTools, (ExamTool o1, ExamTool o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        for (ExamTool tool : examTools) {
            items.add(tool.getName(), tool.getName());
        }
        return items;
    }

    /**
     * fills the ListBoxModel with all ExamModelConfigs
     *
     * @return ListBoxModel
     */
    public ListBoxModel doFillExamModelItems() {
        ListBoxModel items = new ListBoxModel();
        List<ExamModelConfig> models = getModelConfigs();
        models.sort((ExamModelConfig o1, ExamModelConfig o2) -> o1.getName().compareToIgnoreCase(o2.getName()));

        for (ExamModelConfig model : models) {
            items.add(model.getDisplayName(), model.getName());
        }
        return items;
    }

    /**
     * Validates the parameter ModelConfiguration. Checks if it is an id, uuid or
     * exam fullscopename
     *
     * @param value String
     * @return FormValidation
     */
    public FormValidation doCheckModelConfiguration(@QueryParameter String value) {
        return Util.validateElementForSearch(value);
    }

}
