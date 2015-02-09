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

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class NdependResultParserTest {

  private File getTestDoc() throws Exception {
    return new File(getClass().getResource("/CodeRuleResult.xml").toURI());
  }

  @Test
  public void getGroupAndRows() throws Exception {
    final NdependResultParser parser = NdependResultParser.fromFile(getTestDoc());

    assertThat(parser.getQueries().getLength()).isEqualTo(2);
    assertThat(parser.getQueryRows(parser.getQueries().item(0)).getLength()).isEqualTo(2);
  }

  @Test
  public void testParse() throws Exception {
    final NdependResultParser parser = NdependResultParser.fromFile(getTestDoc());
    final List<NdependIssue> issues = parser.parse();
    assertThat(issues.size()).isEqualTo(3);
    final NdependIssue sampleIssue = issues.get(0);
    assertThat(sampleIssue.getRuleKey()).isEqualTo("Method with too many parameters");
    assertThat(sampleIssue.getFile().getPath()).contains("WorkflowPlayer.cs");
    assertThat(sampleIssue.getLine()).isEqualTo(69);
  }
}
