package jenkins.task;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.FreeStyleProject;
import hudson.model.Run;
import hudson.util.ArgumentListBuilder;
import jenkins.internal.data.ReportConfiguration;
import jenkins.internal.data.TestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

public class ExamTaskHelperTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private ExamTaskHelper testObject;
    private FreeStyleProject examTestProject;

    @Mock
    private static Run runMock;
    @Mock
    private static EnvVars envVarsMock;

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        testObject = new ExamTaskHelper(runMock, envVarsMock);
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    @WithoutJenkins
    public void copyArtifactsToTarget() throws Exception {
        FilePath workspace = new FilePath(new File("c:\\my\\path\\to\\workspace"));

        TestConfiguration tc = new TestConfiguration();
        prepareExamReportConfig(tc);
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("cmd.exe", "/C");

        testObject.copyArtifactsToTarget(workspace, tc);
    }

    @Test
    @WithoutJenkins
    public void toWindowsCommand() throws Exception {
        int port = 8085;

        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("cmd.exe", "/C");
        ArgumentListBuilder converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        assertEquals(args.toList(), converted.toList());

        args.add("--launcher.appendVmargs", "-vmargs", "-DUSE_CONSOLE=true", "-DRESTAPI=true",
                "-DRESTAPI_PORT=" + port);
        converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        assertEquals(args.toList(), converted.toList());

        args.add("-DPath=\"C:\\this\\is\\my\\path\"");
        converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        assertEquals(args.toList(), converted.toList());

        args.add("-Dnothing=");
        List<String> expected = args.toList();
        expected.remove("-Dnothing=");
        expected.add("-Dnothing=\"\"");
        converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        assertEquals(expected, converted.toList());

        args.add("password", true);
        converted = Whitebox.invokeMethod(testObject, "toWindowsCommand", args);
        expected.remove("password");
        expected.add("******");
        String sExpected = expected.toString().replaceAll(",", "");
        sExpected = sExpected.replaceAll("\\]", "");
        sExpected = sExpected.replaceAll("\\[", "");
        String sConverted = converted.toString();
        assertEquals(sExpected, sConverted);
    }

    private void prepareExamReportConfig(TestConfiguration config) {
        ReportConfiguration rep = new ReportConfiguration();
        rep.setProjectName("examReport");
        rep.setDbHost("host");
        rep.setDbPort(1234);
        rep.setDbSchema("schema");
        rep.setDbPassword("pass");
        rep.setDbType("type");
        rep.setDbUser("user");
        rep.setDbService("service");
        config.setReportProject(rep);
    }

}
