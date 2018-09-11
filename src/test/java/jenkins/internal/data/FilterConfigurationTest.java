package jenkins.internal.data;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FilterConfigurationTest {

    private FilterConfiguration testObject;

    @Test
    public void emptyConstructorTest() {
        testObject = new FilterConfiguration();
        List<TestrunFilter> filter = Whitebox.getInternalState(testObject, "testrunFilter");
        assertTrue(filter.isEmpty());
    }

    @Test
    public void constructorTest() {
        TestrunFilter filter1 = new TestrunFilter("testrunfilter1", "value1", true, true);
        TestrunFilter filter2 = new TestrunFilter("testrunfilter2", "value2", false, true);
        TestrunFilter filter3 = new TestrunFilter("testrunfilter3", "value3", false, false);
        TestrunFilter filter4 = new TestrunFilter("testrunfilter4", "value4", true, false);

        List<TestrunFilter> trf = Arrays.asList(filter1, filter2, filter3, filter4);

        testObject = new FilterConfiguration(trf);

        List<TestrunFilter> toTest = Whitebox.getInternalState(testObject, "testrunFilter");
        assertEquals(toTest, trf);
    }

    @Test
    public void getTestrunFilter() {
        TestrunFilter filter1 = new TestrunFilter("testrunfilter1", "value1", true, true);
        TestrunFilter filter2 = new TestrunFilter("testrunfilter2", "value2", false, true);
        TestrunFilter filter3 = new TestrunFilter("testrunfilter3", "value3", false, false);
        TestrunFilter filter4 = new TestrunFilter("testrunfilter4", "value4", true, false);

        List<TestrunFilter> trfList = Arrays.asList(filter1, filter2, filter3, filter4);
        testObject = new FilterConfiguration();

        Whitebox.setInternalState(testObject, "testrunFilter", trfList);
        List<TestrunFilter> toTest = testObject.getTestrunFilter();
        assertEquals(toTest, trfList);
    }

    @Test
    public void setTestrunFilterAsList() {
        TestrunFilter filter1 = new TestrunFilter("testrunfilter1", "value1", true, true);
        TestrunFilter filter2 = new TestrunFilter("testrunfilter2", "value2", false, true);
        TestrunFilter filter3 = new TestrunFilter("testrunfilter3", "value3", false, false);
        TestrunFilter filter4 = new TestrunFilter("testrunfilter4", "value4", true, false);

        List<TestrunFilter> trfList = Arrays.asList(filter1, filter2, filter3, filter4);

        testObject = new FilterConfiguration();
        testObject.setTestrunFilter(trfList);

        List<TestrunFilter> toTest = Whitebox.getInternalState(testObject, "testrunFilter");

        assertEquals(toTest, trfList);
    }

    @Test
    public void setTestrunFilter() {
        TestrunFilter filter1 = new TestrunFilter("testrunfilter1", "value1", true, true);

        testObject = new FilterConfiguration();
        testObject.setTestrunFilter(filter1);

        List<TestrunFilter> toTest = Whitebox.getInternalState(testObject, "testrunFilter");

        assertEquals(toTest, Collections.singletonList(filter1));
    }

    @Test
    public void addTestrunFilter() {
        // create testobjects
        TestrunFilter filter1 = new TestrunFilter("testrunfilter1", "value1", true, true);
        TestrunFilter filter2 = new TestrunFilter("testrunfilter2", "value2", false, true);
        TestrunFilter filter3 = new TestrunFilter("testrunfilter3", "value3", false, false);
        TestrunFilter filter4 = new TestrunFilter("testrunfilter4", "value4", true, false);

        TestrunFilter addFilter1 = new TestrunFilter("addFilter1", "addValue1", false, false);
        TestrunFilter addFilter2 = new TestrunFilter("addFilter2", "addValue2", true, false);

        List<TestrunFilter> trfList = new ArrayList<>();
        trfList.add(filter1);
        trfList.add(filter2);
        trfList.add(filter3);
        trfList.add(filter4);

        // initialize testObject
        testObject = new FilterConfiguration();

        // set propertie
        Whitebox.setInternalState(testObject, "testrunFilter", trfList);
        List<TestrunFilter> toTest1 = Whitebox.getInternalState(testObject, "testrunFilter");

        assertEquals(trfList, toTest1);

        testObject.addTestrunFilter(addFilter1);
        List<TestrunFilter> toTest2 = Whitebox.getInternalState(testObject, "testrunFilter");
        trfList.add(addFilter1);
        assertEquals(trfList, toTest2);

        testObject.addTestrunFilter(addFilter2);
        List<TestrunFilter> toTest3 = Whitebox.getInternalState(testObject, "testrunFilter");
        trfList.add(addFilter2);
        assertEquals(trfList, toTest3);
    }
}
