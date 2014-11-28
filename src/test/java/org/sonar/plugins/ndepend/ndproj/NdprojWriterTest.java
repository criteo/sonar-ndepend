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
import org.sonar.plugins.ndepend.NdependQuery;
import org.sonar.plugins.ndepend.NdependQuery.Scope;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.fest.assertions.Assertions.assertThat;

public class NdprojWriterTest {

  @Test
  public void TestWriteTo() throws Exception {
    Writer writer = new StringWriter();
    NdprojInfo solutionInfo = new NdprojInfo(Arrays.asList("a1", "a2"),
      Arrays.asList("f1", "f2"), Arrays.asList("d1", "d2"));
    NdependQuery query = new NdependQuery("name", "group", Scope.METHOD,
      "warnif count > 0 from m in JustMyCode.Methods");
    new NdprojWriter(solutionInfo, Arrays.asList(query), new File("output"))
    .writeTo(writer);

    String xml = writer.toString();
    Pattern p = Pattern
      .compile(
        ".*<NDepend.*<OutputDir.*<Assemblies>.*<FrameworkAssemblies>.*<Dirs>.*<Queries>.*</NDepend>.*",
        Pattern.MULTILINE | Pattern.DOTALL);
    assertThat(p.matcher(xml).matches()).overridingErrorMessage(
      "Wrong XML: " + xml).isTrue();
  }
}
