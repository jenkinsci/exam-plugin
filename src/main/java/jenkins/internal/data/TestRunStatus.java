package jenkins.internal.data;

import java.io.Serializable;

public class TestRunStatus implements Serializable {
    /**
     * Properties
     */
    private String tcName;
    private String tcFullScopedName;
    private int testCaseCount;
    private int currentTestCaseIdx;
    private String expectedRuntime;
    private String currentRuntime;
    private String remainingRuntime;

    /**
     * Gets the name of the currently running Testcase.
     * @return the name.
     */
    public String getName() {
        return this.tcName;
    }

    /**
     * Sets the name of the current Testcase.
     * @param name
     *            the name.
     */
    public void setName(String name) {
        this.tcName = name;
    }

    /**
     * Gets the fullscopedname of the currently running Testcase.
     * @return the fullscopedname.
     */
    public String getFullScopedName() {
        return this.tcFullScopedName;
    }

    /**
     * Sets the fullscopedname of the current Testcase.
     * @param fullScopedName
     *            the fullscopedname.
     */
    public void setFullScopedName(String fullScopedName) {
        this.tcFullScopedName = fullScopedName;
    }

    /**
     * Gets the testCaseCount.
     * @return the testCaseCount.
     */
    public int getTestCaseCount() {
        return this.testCaseCount;
    }

    /**
     * Sets the testCaseCount.
     * @param testCaseCount
     *            the testCaseCount.
     */
    public void setTestCaseCount(int testCaseCount) {
        this.testCaseCount = testCaseCount;
    }

    /**
     * Gets the current Testcase index.
     * @return the index.
     */
    public int getCurrentTestCaseIdx() {
        return this.currentTestCaseIdx;
    }

    /**
     * Sets the current Testcase index.
     * @param currentTestCaseIdx
     *            the index.
     */
    public void setCurrentTestCaseIdx(int currentTestCaseIdx) {
        this.currentTestCaseIdx = currentTestCaseIdx;
    }

    /**
     * Gets the expected Runtime as String.
     * @return the expected Runtime.
     */
    public String getExpectedRuntime() {
        return this.expectedRuntime;
    }

    /**
     * Sets the expected Runtime.
     * @param expectedRuntime
     *            the expected Runtime.
     */
    public void setExpectedRuntime(String expectedRuntime) {
        this.expectedRuntime = expectedRuntime;
    }

    /**
     * Gets the current Runtime.
     * @return the current Runtime.
     */
    public String getCurrentRuntime() {
        return this.currentRuntime;
    }

    /**
     * Sets the current Runtime.
     * @param currentRuntime
     *            the current Runtime.
     */
    public void setCurrentRuntime(String currentRuntime) {
        this.currentRuntime = currentRuntime;
    }

    /**
     * Gets the remaining Runtime.
     * @return the remaining Runtime.
     */
    public String getRemainingRuntime() {
        return this.remainingRuntime;
    }

    /**
     * Sets the remaining Runtime.
     * @param remainingRuntime
     *            the remaining Runtime.
     */
    public void setRemainingRuntime(String remainingRuntime) {
        this.remainingRuntime = remainingRuntime;
    }
}
