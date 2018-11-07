package jenkins.internal.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuration of EXAM Testrunfilter
 */
public class FilterConfiguration {

    private List<TestrunFilter> testrunFilter;

    /**
     * Constructor of Configuration of EXAM Testrunfilter
     */
    public FilterConfiguration() {
        this.testrunFilter = new ArrayList<>();
    }

    /**
     * Constructor of Configuration of EXAM Testrunfilter
     *
     * @param testrunFilter
     */
    public FilterConfiguration(List<TestrunFilter> testrunFilter) {
        this.testrunFilter = testrunFilter;
    }

    public List<TestrunFilter> getTestrunFilter() {
        return this.testrunFilter;
    }

    /**
     * Sets the Configuration of EXAM Testrunfilter
     *
     * @param testrunFilter
     */
    public void setTestrunFilter(TestrunFilter testrunFilter) {
        this.testrunFilter = Collections.singletonList(testrunFilter);
    }

    /**
     * Sets the Configuration of EXAM Testrunfilter
     *
     * @param testrunFilter
     */
    public void setTestrunFilter(List<TestrunFilter> testrunFilter) {
        this.testrunFilter = testrunFilter;
    }

    /**
     * Add a EXAM Testrunfilter to the configuration list
     *
     * @param testrunFilter
     */
    public void addTestrunFilter(TestrunFilter testrunFilter) {
        this.testrunFilter.add(testrunFilter);
    }
}
