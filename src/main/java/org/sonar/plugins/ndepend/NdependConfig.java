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

public class NdependConfig {

  // Hide constructor of this utility class
  private NdependConfig() {
  }

  public static final String LANGUAGE_KEY = "cs";

  public static final String REPOSITORY_KEY = "cs-ndepend";

  public static final String SOLUTION_PATH_PROPERTY_KEY = "sonar.ndepend.solutionFile";
  public static final String NDEPEND_PATH_PROPERTY_KEY = "sonar.ndepend.ndependPath";
  public static final String NDEPEND_RULES_URL_KEY = "sonar.ndepend.rulesUri";

  public static final String CATEGORY = "Ndepend";

  public static final String NDEPEND_RESULTS_FILENAME = "XmlFilesUsedToBuildReport/CodeRuleResult.xml";
  public static final String NDEPEND_RESULTS_FOLDER = "ndepend";
}
