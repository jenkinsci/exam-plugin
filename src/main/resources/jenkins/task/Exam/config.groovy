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
package jenkins.task

import jenkins.internal.enumeration.RestAPILogLevelEnum

f = namespace(lib.FormTagLib)


f.section(title: _("Modeler")) {
    if (descriptor.installations.length != 0) {
        f.entry(title: _("EXAM Version"), help: "/descriptor/jenkins.task.Exam/help/examName") {
            select(class: "setting-input", name: "exam.examName") {
                option(value: "(Default)", _("Default"))
                descriptor.getInstallations().each {
                    f.option(selected: it.name == instance?.examName, value: it.name, it.name)
                }
            }
        }
    }

    f.entry(title: _("Modell"), help: "/descriptor/jenkins.task.Exam/help/examModel") {
        select(class: "setting-input", name: "exam.examModel") {
            descriptor.getModelConfigs().each {
                f.option(selected: it.name == instance?.examModel, value: it.name, it.displayName)
            }
        }
    }

    f.entry(title: _("delete project"), field: "clearWorkspace") {
        f.checkbox()
    }
    f.advanced() {

        f.entry(title: _("Java Opts"), field: "javaOpts") {
            f.textbox()
        }
    }
}

f.section(title: _("Testrun")) {

    if (descriptor.pythonInstallations.length != 0) {
        f.entry(title: _("Python Version"), help: "/descriptor/jenkins.task.Exam/help/pythonName") {
            select(class: "setting-input", name: "exam.pythonName") {
                option(value: "(Default)", _("Default"))
                descriptor.getPythonInstallations().each {
                    f.option(selected: it.name == instance?.pythonName, value: it.name, it.name)
                }
            }
        }
    }

    f.entry(title: _("test object"), field: "executionFile") {
        f.textbox()
    }

    f.entry(title: _("SystemConfiguration"), field: "systemConfiguration") {
        f.textbox()
    }

    f.optionalBlock(title: _("configure Testrun Filters"), inline: "true", help: "/descriptor/jenkins.task.Exam/help/testrunFilter") {
        f.entry(title: "") {
            f.repeatableProperty(
                    field: "testrunFilter",
                    header: "Testrun Filter",
                    add: "Add filter")
        }
    }

    f.optionalBlock(title: _("configure logging"), inline: "true", help: "/descriptor/jenkins.task.Exam/help/logging") {
        f.entry(title: _("TEST_CTRL")) {
            select(class: "setting-input", name: "loglevel_test_ctrl") {
                descriptor.getLogLevels().each {
                    if(instance?.loglevel_test_ctrl == null){
                        f.option(selected: it.name() == descriptor.getDefaultLogLevel(), value: it.name(), it.name())
                    } else {
                        f.option(selected: it.name() == instance?.loglevel_test_ctrl, value: it.name(), it.name())
                    }
                }
            }
        }

        f.entry(title: _("TEST_LOGIC")) {
            select(class: "setting-input", name: "loglevel_test_logic") {
                descriptor.getLogLevels().each {
                    if(instance?.loglevel_test_logic == null){
                        f.option(selected: it.name() == descriptor.getDefaultLogLevel(), value: it.name(), it.name())
                    } else{
                        f.option(selected: it.name() == instance?.loglevel_test_logic, value: it.name(), it.name())
                    }
                }
            }
        }

        f.entry(title: _("LIB_CTRL")) {
            select(class: "setting-input", name: "loglevel_lib_ctrl") {
                descriptor.getLogLevels().each {
                    if(instance?.loglevel_lib_ctrl == null){
                        f.option(selected: it.name() == descriptor.getDefaultLogLevel(), value: it.name(), it.name())
                    } else {
                        f.option(selected: it.name() == instance?.loglevel_lib_ctrl, value: it.name(), it.name())
                    }
                }
            }
        }
    }
}

f.section(title: _("Report")) {
    f.entry(title: _("Reports"), help: "/descriptor/jenkins.task.Exam/help/examReport") {
        select(class: "setting-input", name: "exam.examReport") {
            descriptor.getReportConfigs().each {
                f.option(selected: it.name == instance?.examReport, value: it.name, it.displayName)
            }
        }
    }

    f.entry(title: _("report prefix"), field: "reportPrefix") {
        f.textbox()
    }

    f.optionalBlock(title: _("create pdf report"), inline: "true", field: "pdfReport") {
        f.entry(title: _("report template"), field: "pdfReportTemplate") {
            f.textbox()
        }
        f.entry(title: _("select filter"), field: "pdfSelectFilter") {
            f.textbox()
        }
        f.entry(title: _("include measure images"), field: "pdfMeasureImages") {
            f.checkbox()
        }
    }
}
