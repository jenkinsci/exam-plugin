package jenkins.internal.data;

import jenkins.task.TestrunFilter;

import java.util.ArrayList;
import java.util.List;

public class FilterConfiguration {

    private List<TestrunFilter> testrunFilter;

    public FilterConfiguration() {
        this.testrunFilter = new ArrayList<>();
    }

    public FilterConfiguration(List<TestrunFilter> testrunFilter) {
        this.testrunFilter = testrunFilter;
    }

    public List<TestrunFilter> getTestrunFilter() {
        return this.testrunFilter;
    }

    public void setTestrunFilter(List<TestrunFilter> testrunFilter) {
        this.testrunFilter = testrunFilter;
    }

    public void addTestrunFilter(TestrunFilter testrunFilter) {
        this.testrunFilter.add(testrunFilter);
    }
}
