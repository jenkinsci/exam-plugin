package jenkins.report;

import hudson.model.Action;
import hudson.model.Run;
import jenkins.internal.enumeration.RestAPILogLevelEnum;
import jenkins.model.RunAction2;
import jenkins.task.ExamTask;
import jenkins.task.TestrunFilter;

import javax.annotation.CheckForNull;
import java.util.List;

public class ExamReportAction implements Action, RunAction2 {

    private transient Run run;
    private ExamTask examTask;

    public ExamReportAction(ExamTask examTask) {
        this.examTask = examTask;
    }

    public String getPythonName(){
        return examTask.getPythonName();
    }

    public String getExamName() {
        return examTask.getExamName();
    }

    public List<TestrunFilter> getTestrunFilter() {
        return examTask.getTestrunFilter();
    }


    public String getLoglevelTestCtrl(){
        return examTask.getLoglevelTestCtrl();
    }

    public String getLoglevelTestLogic(){
        return examTask.getLoglevelTestLogic();
    }

    public String getLoglevelLibCtrl(){
        return examTask.getLoglevelLibCtrl();
    }

    @CheckForNull
    @Override
    public String getIconFileName() {
        return "/plugin/exam/images/exam.jpg";
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
