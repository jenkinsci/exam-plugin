/**
 * Copyright (c) 2018 MicroNova AG
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *        list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this
 *        list of conditions and the following disclaimer in the documentation and/or
 *        other materials provided with the distribution.
 *
 *     3. Neither the name of MicroNova AG nor the names of its
 *        contributors may be used to endorse or promote products derived from
 *        this software without specific prior written permission.
 *
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
