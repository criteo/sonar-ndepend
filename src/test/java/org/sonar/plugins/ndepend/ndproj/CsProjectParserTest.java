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
package org.sonar.plugins.ndepend.ndproj;

import org.junit.Test;
import org.sonar.plugins.ndepend.ndproj.CsProjectInfo;
import org.sonar.plugins.ndepend.ndproj.CsProjectParseError;
import org.sonar.plugins.ndepend.ndproj.CsProjectParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.fest.assertions.Assertions.assertThat;

public class CsProjectParserTest {

  @Test
  public void parse() throws CsProjectParseError, IOException {
    CsProjectParser parser = new CsProjectParser();
    File projectFile = new File("src/test/resources/test.csproj");
    String projectDir = projectFile.getParentFile().getCanonicalPath();
    CsProjectInfo projectInfo = parser.parse(projectFile);
    assertThat(projectInfo.getAssemblyName()).isEqualTo("TestProject");
    assertThat(new ArrayList<String>(projectInfo.getOutputPaths())).containsOnly(
      new File(projectDir, "bin\\Debug\\").getCanonicalPath(),
      new File(projectDir, "bin\\Release\\").getCanonicalPath()
      );

    assertThat(new ArrayList<String>(projectInfo.getReferences())).containsOnly(
      "ref1.dll",
      "ref2",
      "ref3",
      "",
      "Moq",
      "Newtonsoft.Json",
      "RabbitMQ.Client"
      );
  }

  @Test(expected = CsProjectParseError.class)
  public void parseInvalidFile() throws CsProjectParseError {
    CsProjectParser parser = new CsProjectParser();
    parser.parse(new File("src/test/resources/sample.sln"));
  }
}
