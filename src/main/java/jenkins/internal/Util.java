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
package jenkins.internal;

import hudson.FilePath;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.util.FormValidation;
import jenkins.internal.enumeration.PYTHON_WORDS;
import jenkins.model.Jenkins;
import jenkins.task._exam.Messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static Node workspaceToNode(FilePath workspace) {
        Jenkins j = Jenkins.getInstance();
        if (workspace != null && workspace.isRemote()) {
            for (Computer c : j.getComputers()) {
                if (c.getChannel() == workspace.getChannel()) {
                    Node n = c.getNode();
                    if (n != null) {
                        return n;
                    }
                }
            }
        }
        return j;
    }

    public static boolean isUuidValid(String uuid) {
        String myUuid = uuid.replaceAll("-", "");
        if (myUuid.length() != 32) {
            return false;
        }
        Pattern regexSystemConfig = Pattern.compile("[0-9a-f]{32}");
        Matcher matcher = regexSystemConfig.matcher(myUuid);
        if (matcher.find()) {
            if (matcher.groupCount() == 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIdValid(String object) {
        Pattern regexSystemConfig = Pattern.compile("^I[0-9]+");
        Matcher matcher = regexSystemConfig.matcher(object);
        if (matcher.find()) {
            if (matcher.groupCount() == 0) {
                return true;
            }
        }
        return false;
    }


    public static boolean isPythonConformName(String name) {
        if (name == null) {
            return false;
        }

        String[] splitted = name.split("\\.");

        if (splitted.length == 1 && name.startsWith("I")) {
            return false;
        }

        Pattern regexPattern = Pattern.compile("[_a-zA-Z@]+[_a-zA-Z0-9#@]*");

        for (String part : splitted) {
            if (!regexPattern.matcher(part).matches()) {
                return false;
            }
            PYTHON_WORDS id = PYTHON_WORDS.get(part);
            if (PYTHON_WORDS.RESERVED_WORDS.contains(id)) {
                return false;
            }
        }
        return true;
    }


    public static FormValidation validateId(String value) {

        if (isIdValid(value)) {
            return FormValidation.ok();
        }

        return FormValidation.error(Messages.EXAM_RegExId());
    }


    public static FormValidation validatePythonConformName(String value) {

        if (isPythonConformName(value)) {
            return FormValidation.ok();
        }

        return FormValidation.error(Messages.EXAM_RegExFsn());
    }

    public static FormValidation validateUuid(String value) {

        if (isUuidValid(value)) {
            return FormValidation.ok();
        }

        return FormValidation.error(Messages.EXAM_RegExUuid());
    }

    public static FormValidation validateElementForSearch(String value) {
        StringBuilder errorMsg = new StringBuilder("");

        boolean uuidValid = isUuidValid(value);
        boolean idValid = isIdValid(value);
        boolean fsnValid = isPythonConformName(value);

        if (uuidValid || idValid || fsnValid) {
            return FormValidation.ok();
        }

        errorMsg.append(Messages.EXAM_RegExUuid());
        errorMsg.append("\r\n");
        errorMsg.append(Messages.EXAM_RegExId());
        errorMsg.append("\r\n");
        errorMsg.append(Messages.EXAM_RegExFsn());
        errorMsg.append("\r\n");

        return FormValidation.error(errorMsg.toString());
    }
}
