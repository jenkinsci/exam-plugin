package jenkins.task;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import jenkins.plugins.exam.config.ExamPluginConfig;
import org.junit.*;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.fail;

public class CleanExamTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();
    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();
    private CleanExam testObject;
    private FreeStyleProject examJenkinsProject;

    @Before
    public void setUp() throws Exception {
        testObject = new CleanExam();
        examJenkinsProject = j.createFreeStyleProject("cleanExamTest");
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    public void perform() throws Exception {
        // add jenkins build steps
        this.addPrebuildStep();
        examJenkinsProject.getBuildersList().add(testObject);

        FreeStyleBuild build = examJenkinsProject.scheduleBuild2(0).get();

        // target dir should be deleted now
        List<FilePath> directories = build.getWorkspace().listDirectories();
        directories.forEach(x -> {
            if (x.getName().contains("target")) {
                fail("target Directory was not deleted. It is still in the workspace");
            }
        });
    }

    //#region Helpermethod

    private void addPrebuildStep() {
        examJenkinsProject.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
                listener.getLogger().println("PreBuildStep!!");
                try {
                    FilePath workspace = build.getWorkspace();
                    FilePath target = workspace.createTempDir("target", " ");
                    if (!target.exists()) {
                        target.mkdirs();
                    }
                    if (target.isDirectory()) {
                        listener.getLogger().println("directory was created");
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    //#endregion

}