package jenkins.report;

import hudson.model.Action;
import hudson.model.Run;
import jenkins.model.RunAction2;

import javax.annotation.CheckForNull;

public class ExamReportAction implements Action, RunAction2 {

    private transient Run run;
    private String name;

    public ExamReportAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @CheckForNull
    @Override
    public String getIconFileName() {
        return "document.png";
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        return "EXAM build";
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        return "EXAM_build";
    }

    @Override
    public void onAttached(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        this.run = run;
    }

    public Run getRun() {
        return run;
    }
}
