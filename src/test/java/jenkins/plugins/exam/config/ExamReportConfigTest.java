package jenkins.plugins.exam.config;

import Utils.Whitebox;
import hudson.util.Secret;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

import static org.junit.Assert.assertEquals;

public class ExamReportConfigTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    ExamReportConfig testObject;

    @Before
    public void setUp() {
        testObject = new ExamReportConfig();
    }

    @After
    public void tearDown() {
        testObject = null;
    }

    @Test
    @WithoutJenkins
    public void getName() {
        String testName = "testName";
        Whitebox.setInternalState(testObject, "name", testName);
        String setName = testObject.getName();

        assertEquals(testName, setName);
    }

    @Test
    @WithoutJenkins
    public void setName() {
        String testName = "testName";
        testObject.setName(testName);
        String setName = Whitebox.getInternalState(testObject, "name");

        assertEquals(testName, setName);
    }

    @Test
    @WithoutJenkins
    public void getDbType() {
        String testDbType = "testDbType";
        Whitebox.setInternalState(testObject, "dbType", testDbType);
        String setDbType = testObject.getDbType();

        assertEquals(testDbType, setDbType);
    }

    @Test
    @WithoutJenkins
    public void setDbType() {
        String testDbType = "testDbType";
        testObject.setDbType(testDbType);
        String setDbtype = Whitebox.getInternalState(testObject, "dbType");

        assertEquals(testDbType, setDbtype);
    }

    @Test
    @WithoutJenkins
    public void getHost() {
        String testHost = "testHost";
        Whitebox.setInternalState(testObject, "host", testHost);
        String setHost = testObject.getHost();

        assertEquals(testHost, setHost);
    }

    @Test
    @WithoutJenkins
    public void setHost() {
        String testHost = "testHost";
        testObject.setHost(testHost);
        String setHost = Whitebox.getInternalState(testObject, "host");

        assertEquals(testHost, setHost);
    }

    @Test
    @WithoutJenkins
    public void getPort() {
        String testPort = "1234";
        Whitebox.setInternalState(testObject, "port", testPort);
        String setPort = testObject.getPort();

        assertEquals(testPort, setPort);
    }

    @Test
    @WithoutJenkins
    public void setPort() {
        String testPort = "5678";
        testObject.setPort(testPort);
        String setPort = Whitebox.getInternalState(testObject, "port");

        assertEquals(testPort, setPort);
    }

    @Test
    @WithoutJenkins
    public void getServiceOrSid() {
        String testServiceOrSid = "testServiceOrSid";
        Whitebox.setInternalState(testObject, "serviceOrSid", testServiceOrSid);
        String setPort = testObject.getServiceOrSid();

        assertEquals(testServiceOrSid, setPort);
    }

    @Test
    @WithoutJenkins
    public void setServiceOrSid() {
        String testServiceOrSid = "testServiceOrSid";
        testObject.setServiceOrSid(testServiceOrSid);
        String setServiceOrSid = Whitebox.getInternalState(testObject, "serviceOrSid");

        assertEquals(testServiceOrSid, setServiceOrSid);
    }

    @Test
    @WithoutJenkins
    public void getSchema() {
        String testSchema = "testSchema";
        Whitebox.setInternalState(testObject, "schema", testSchema);
        String setSchema = testObject.getSchema();

        assertEquals(testSchema, setSchema);
    }

    @Test
    @WithoutJenkins
    public void setSchema() {
        String testSchema = "testSchema";
        testObject.setSchema(testSchema);
        String setSchema = Whitebox.getInternalState(testObject, "schema");

        assertEquals(testSchema, setSchema);
    }

    @Test
    @WithoutJenkins
    public void getDbUser() {
        String testDbUser = "testDbUser";
        Whitebox.setInternalState(testObject, "dbUser", testDbUser);
        String setDbUser = testObject.getDbUser();

        assertEquals(testDbUser, setDbUser);
    }

    @Test
    @WithoutJenkins
    public void setDbUser() {
        String testDbUser = "testDbUser";
        testObject.setDbUser(testDbUser);
        String setSchema = Whitebox.getInternalState(testObject, "dbUser");

        assertEquals(testDbUser, setSchema);
    }

    @Test
    public void getDbPass() {
        String testDbPass = "testDbPass";
        Whitebox.setInternalState(testObject, "dbPass", Secret.fromString(testDbPass));
        Secret setDbPass = testObject.getDbPass();

        assertEquals(testDbPass, setDbPass.getPlainText());
    }

    @Test
    public void setDbPass() {
        String testDbPass = "testDbPass";
        testObject.setDbPass(Secret.fromString(testDbPass));
        Secret setDbPass = Whitebox.getInternalState(testObject, "dbPass");

        assertEquals(testDbPass, setDbPass.getPlainText());
    }

    @Test
    @WithoutJenkins
    public void getDisplayName() {
        String testName = "testName";
        String testSchema = "testSchema";
        String testHost = "testHost";
        String testPort = "1234";
        String expectedResult = testName + " -- ( " + testSchema + "@" + testHost + ":" + testPort + " )";

        Whitebox.setInternalState(testObject, "name", testName);
        Whitebox.setInternalState(testObject, "schema", testSchema);
        Whitebox.setInternalState(testObject, "host", testHost);
        Whitebox.setInternalState(testObject, "port", testPort);

        String result = testObject.getDisplayName();

        assertEquals(expectedResult, result);
    }
}
