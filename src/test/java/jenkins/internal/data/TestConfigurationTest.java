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

import jenkins.internal.enumeration.RestAPILogLevelEnum;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class TestConfigurationTest {
    
    private TestConfiguration testObject;
    private final static String TESTSTRING = "myTestString";
    private RestAPILogLevelEnum testLevel;
    private final static ModelConfiguration MODELCONFIG = new ModelConfiguration() {{
        setModelName(TESTSTRING);
        setTargetEndpoint(TESTSTRING);
        setProjectName(TESTSTRING);
    }};
    
    private final static ReportConfiguration REPORTCONFIG = new ReportConfiguration() {{
        setProjectName(TESTSTRING);
        setDbHost(TESTSTRING);
        setDbSchema(TESTSTRING);
    }};
    
    @Before
    public void setUp() throws Exception {
        testObject = new TestConfiguration();
        List<Integer> givenList = Arrays.asList(0, 10, 15, 20, 25, 30);
        Random rand = new Random();
        int randomElement = givenList.get(rand.nextInt(givenList.size()));
        testLevel = RestAPILogLevelEnum.fromInt(randomElement);
    }
    
    @Test
    public void getReportPrefix() {
        Whitebox.setInternalState(testObject, "reportPrefix", TESTSTRING);
        String testIt = testObject.getReportPrefix();
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void setReportPrefix() {
        testObject.setReportPrefix(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "reportPrefix");
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void getModelProject() {
        Whitebox.setInternalState(testObject, "modelProject", MODELCONFIG);
        ModelConfiguration testIt = testObject.getModelProject();
        assertEquals(MODELCONFIG.getModelName(), testIt.getModelName());
        assertEquals(MODELCONFIG.getTargetEndpoint(), testIt.getTargetEndpoint());
        assertEquals(MODELCONFIG.getProjectName(), testIt.getProjectName());
    }
    
    @Test
    public void setModelProject() {
        testObject.setModelProject(MODELCONFIG);
        ModelConfiguration testIt = Whitebox.getInternalState(testObject, "modelProject");
        assertEquals(MODELCONFIG.getModelName(), testIt.getModelName());
        assertEquals(MODELCONFIG.getTargetEndpoint(), testIt.getTargetEndpoint());
        assertEquals(MODELCONFIG.getProjectName(), testIt.getProjectName());
    }
    
    @Test
    public void getReportProject() {
        Whitebox.setInternalState(testObject, "reportProject", REPORTCONFIG);
        ReportConfiguration testIt = testObject.getReportProject();
        assertEquals(REPORTCONFIG.getProjectName(), testIt.getProjectName());
        assertEquals(REPORTCONFIG.getDbHost(), testIt.getDbHost());
        assertEquals(REPORTCONFIG.getDbSchema(), testIt.getDbSchema());
    }
    
    @Test
    public void setReportProject() {
        testObject.setReportProject(REPORTCONFIG);
        ReportConfiguration testIt = Whitebox.getInternalState(testObject, "reportProject");
        assertEquals(REPORTCONFIG.getProjectName(), testIt.getProjectName());
        assertEquals(REPORTCONFIG.getDbHost(), testIt.getDbHost());
        assertEquals(REPORTCONFIG.getDbSchema(), testIt.getDbSchema());
    }
    
    @Test
    public void getSystemConfig() {
        Whitebox.setInternalState(testObject, "systemConfig", TESTSTRING);
        String testIt = testObject.getSystemConfig();
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void setSystemConfig() {
        testObject.setSystemConfig(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "systemConfig");
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void getModelConfig() {
        Whitebox.setInternalState(testObject, "modelConfig", TESTSTRING);
        String testIt = testObject.getModelConfig();
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void setModelConfig() {
        testObject.setModelConfig(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "modelConfig");
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void getTestObject() {
        Whitebox.setInternalState(testObject, "testObject", TESTSTRING);
        String testIt = testObject.getTestObject();
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void setTestObject() {
        testObject.setTestObject(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "testObject");
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void getlogLevelTC() {
        Whitebox.setInternalState(testObject, "logLevelTC", testLevel);
        RestAPILogLevelEnum testIt = testObject.getLogLevelTC();
        assertEquals(testLevel, testIt);
    }
    
    @Test
    public void setlogLevelTC() {
        testObject.setLogLevelTC(testLevel);
        RestAPILogLevelEnum testIt = Whitebox.getInternalState(testObject, "logLevelTC");
        assertEquals(testLevel, testIt);
    }
    
    @Test
    public void getlogLevelTL() {
        Whitebox.setInternalState(testObject, "logLevelTL", testLevel);
        RestAPILogLevelEnum testIt = testObject.getLogLevelTL();
        assertEquals(testLevel, testIt);
    }
    
    @Test
    public void setlogLevelTL() {
        testObject.setLogLevelTL(testLevel);
        RestAPILogLevelEnum testIt = Whitebox.getInternalState(testObject, "logLevelTL");
        assertEquals(testLevel, testIt);
    }
    
    @Test
    public void getLogLevelLC() {
        Whitebox.setInternalState(testObject, "logLevelLC", testLevel);
        RestAPILogLevelEnum testIt = testObject.getLogLevelLC();
        assertEquals(testLevel, testIt);
    }
    
    @Test
    public void setLogLevelLC() {
        testObject.setLogLevelLC(testLevel);
        RestAPILogLevelEnum testIt = Whitebox.getInternalState(testObject, "logLevelLC");
        assertEquals(testLevel, testIt);
    }
    
    @Test
    public void getPythonPath() {
        Whitebox.setInternalState(testObject, "pythonPath", TESTSTRING);
        String testIt = testObject.getPythonPath();
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void setPythonPath() {
        testObject.setPythonPath(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "pythonPath");
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void getPdfReportTemplate() {
        Whitebox.setInternalState(testObject, "pdfReportTemplate", TESTSTRING);
        String testIt = testObject.getPdfReportTemplate();
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void setPdfReportTemplate() {
        testObject.setPdfReportTemplate(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "pdfReportTemplate");
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void getPdfSelectFilter() {
        Whitebox.setInternalState(testObject, "pdfSelectFilter", TESTSTRING);
        String testIt = testObject.getPdfSelectFilter();
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void setPdfSelectFilter() {
        testObject.setPdfSelectFilter(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "pdfSelectFilter");
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void getPdfMeasureImages() {
        Whitebox.setInternalState(testObject, "pdfMeasureImages", true);
        boolean testIt = testObject.getPdfMeasureImages();
        assertTrue(testIt);
        
        Whitebox.setInternalState(testObject, "pdfMeasureImages", false);
        boolean testIt2 = testObject.getPdfMeasureImages();
        assertFalse(testIt2);
    }
    
    @Test
    public void setPdfMeasureImages() {
        testObject.setPdfMeasureImages(true);
        boolean testIt = Whitebox.getInternalState(testObject, "pdfMeasureImages");
        assertTrue(testIt);
    }
    
    @Test
    public void getUseExecutionFile() {
        Whitebox.setInternalState(testObject, "useExecutionFile", true);
        boolean testIt = testObject.getUseExecutionFile();
        assertTrue(testIt);
    }
    
    @Test
    public void setUseExecutionFile() {
        testObject.setUseExecutionFile(true);
        boolean testIt = Whitebox.getInternalState(testObject, "useExecutionFile");
        assertTrue(testIt);
    }
    
    @Test
    public void getPathPCode() {
        Whitebox.setInternalState(testObject, "pathPCode", TESTSTRING);
        String testIt = testObject.getPathPCode();
        assertEquals(TESTSTRING, testIt);
    }
    
    @Test
    public void setPathPCode() {
        testObject.setPathPCode(TESTSTRING);
        String testIt = Whitebox.getInternalState(testObject, "pathPCode");
        assertEquals(TESTSTRING, testIt);
    }
}
