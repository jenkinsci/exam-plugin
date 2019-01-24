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
package jenkins.internal;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.util.FormValidation;
import jenkins.internal.enumeration.PythonWords;
import jenkins.model.Jenkins;
import jenkins.task._exam.Messages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    
    /**
     * Get the Node of a workspace
     *
     * @param workspace FilePath
     *
     * @return Node
     */
    public static Node workspaceToNode(FilePath workspace) {
        Jenkins j = Jenkins.getInstance();
        if (workspace != null && workspace.isRemote()) {
            for (Computer c : j.getComputers()) {
                if (c.getChannel() == workspace.getChannel() && c.getNode() != null) {
                    return c.getNode();
                }
            }
        }
        return j;
    }
    
    /**
     * Check uuid pattern
     *
     * @param uuid String
     *
     * @return true, if valid
     */
    public static boolean isUuidValid(String uuid) {
        String myUuid = uuid.replaceAll("-", "");
        if (myUuid.length() != 32) {
            return false;
        }
        Pattern regexSystemConfig = Pattern.compile("[0-9a-f]{32}");
        Matcher matcher = regexSystemConfig.matcher(myUuid);
        if (matcher.find()) {
            return matcher.groupCount() == 0;
        }
        return false;
    }
    
    /**
     * Check id pattern
     *
     * @param id String
     *
     * @return true, if valid
     */
    public static boolean isIdValid(String id) {
        Pattern regexSystemConfig = Pattern.compile("^I[0-9]+");
        Matcher matcher = regexSystemConfig.matcher(id);
        if (matcher.find()) {
            if (matcher.groupCount() == 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check name pattern
     *
     * @param name
     *
     * @return true, if valid
     */
    public static boolean isPythonConformFSN(String name) {
        if (name == null) {
            return false;
        }
        
        String[] splitted = name.split("\\.");
        
        if (splitted.length == 1 && name.startsWith("I")) {
            return false;
        }
        
        for (String part : splitted) {
            if (!isPythonConformName(part)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check name pattern
     *
     * @param name String
     *
     * @return true, if valid
     */
    public static boolean isPythonConformName(String name) {
        Pattern regexPattern = Pattern.compile("[_a-zA-Z@]+[_a-zA-Z0-9#@]*");
        if (!regexPattern.matcher(name).matches()) {
            return false;
        }
        PythonWords id = PythonWords.get(name);
        return !PythonWords.RESERVED_WORDS.contains(id);
    }
    
    /**
     * Check uuid pattern
     *
     * @param value String
     *
     * @return FormValidation
     */
    public static FormValidation validateUuid(String value) {
        
        if (isUuidValid(value)) {
            return FormValidation.ok();
        }
        
        return FormValidation.error(Messages.EXAM_RegExUuid());
    }
    
    /**
     * Check value on id, uuid and python name
     *
     * @param value String
     *
     * @return FormValidation
     */
    public static FormValidation validateElementForSearch(String value) {
        StringBuilder errorMsg = new StringBuilder("");
        
        boolean uuidValid = isUuidValid(value);
        boolean idValid = isIdValid(value);
        boolean fsnValid = isPythonConformFSN(value);
        
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
    
    /**
     * Check value on id, uuid and python name
     *
     * @param value String
     *
     * @return FormValidation
     */
    public static FormValidation validateSystemConfig(@Nonnull String value) {
        
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append(Messages.EXAM_RegExSysConf());
        errorMsg.append("\r\n");
        
        String[] splitted = value.trim().split(" ", 2);
        
        if (splitted.length != 2) {
            return FormValidation.error(errorMsg.toString());
        }
        
        boolean uuidValid = isUuidValid(splitted[0]);
        boolean pythonValid = isPythonConformName(splitted[1]);
        
        if (uuidValid && pythonValid) {
            return FormValidation.ok();
        }
        
        if (!uuidValid) {
            errorMsg.append(Messages.EXAM_RegExUuid());
            errorMsg.append("\r\n");
        }
        if (!pythonValid) {
            errorMsg.append(Messages.EXAM_RegExFsn());
            errorMsg.append("\r\n");
        }
        
        return FormValidation.error(errorMsg.toString());
    }
    
    public static String replaceEnvVars(@Nullable String text, @Nullable EnvVars env) {
        if (text == null || text.isEmpty() || env == null || env.isEmpty()) {
            return text;
        }
        String retString = text;
        Pattern regexSystemConfig = Pattern.compile("%(.*)%|\\$\\{(.*)\\}");
        Matcher matcher = regexSystemConfig.matcher(text);
        while (matcher.find()) {
            String group = matcher.group();
            String sub = matcher.group(1);
            if (group.startsWith("$")) {
                sub = matcher.group(2);
            }
            String replacement = env.getOrDefault(sub, null);
            if (replacement != null) {
                retString = retString.replace(group, replacement);
            }
        }
        return retString;
    }
}
