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
