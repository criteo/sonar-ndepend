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
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.fest.assertions.Assertions.assertThat;

public class NdprojXmlBuilderTest {

  @Test
  public void testBuild() throws Exception {
    NdprojXmlBuilder builder = new NdprojXmlBuilder();
    builder.addOutputDir(new File("outputDir"));
    builder.addAssemblies(Arrays.asList("a1", "a2"));
    builder.addFrameworkAssemblies(Arrays.asList("f1", "f2"));
    builder.addDirs(Arrays.asList("d1", "d2"));
    NdependQuery query = new NdependQuery("name", "group", Scope.METHOD,
      "warnif count > 0 from m in JustMyCode.Methods");
    builder.addQueries(Arrays.asList(query));
    Document doc = builder.getResult();

    String xml = toXmlString(doc);
    Pattern p = Pattern
      .compile(
        ".*<NDepend AppName=\"default\"><OutputDir KeepXmlFiles=\"True\">.*outputDir</OutputDir>"
          + "<Assemblies><Name>a1</Name><Name>a2</Name></Assemblies>"
          + "<FrameworkAssemblies><Name>f1</Name><Name>f2</Name></FrameworkAssemblies>"
          + "<Dirs><Dir>d1</Dir><Dir>d2</Dir></Dirs>"
          + "<Queries><Group .*><Query .*>.*</Query></Group></Queries></NDepend>",
          Pattern.MULTILINE | Pattern.DOTALL);
    assertThat(p.matcher(xml).matches()).overridingErrorMessage(
      "Wrong XML: " + xml).isTrue();
  }

  private static String toXmlString(Document doc) throws TransformerException {
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    StreamResult result = new StreamResult(new StringWriter());
    DOMSource source = new DOMSource(doc);
    transformer.transform(source, result);
    return result.getWriter().toString();
  }
}
