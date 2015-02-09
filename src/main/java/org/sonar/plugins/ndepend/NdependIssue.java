/*
 * SonarQube NDepend Plugin
 * Copyright (C) 2014 Criteo
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.ndepend;

import javax.annotation.Nullable;

import java.io.File;

public class NdependIssue {

  private final String ruleKey;
  private final String message;
  private String codeUnitName;
  private final File file;
  private final int line;

  public NdependIssue(String ruleKey, String message, @Nullable String codeUnitName, @Nullable File file, @Nullable int line) {
    this.ruleKey = ruleKey;
    this.message = message;
    this.codeUnitName = codeUnitName;
    this.file = file;
    this.line = line;
  }

  public String getRuleKey() {
    return ruleKey;
  }

  public String getMessage() {
    return message;
  }

  public String getCodeUnitName() {
    return codeUnitName;
  }

  public File getFile() {
    return file;
  }

  public int getLine() {
    return line;
  }

}
