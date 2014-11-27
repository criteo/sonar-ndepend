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

import static org.fest.assertions.Assertions.assertThat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.plugins.ndepend.NdependQuery.Scope;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class QueryXmlSerializerTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testCanSerializeQuery() throws Exception {
    Document doc = getEmptyDocument();
    QueryXmlSerializer serializer = new QueryXmlSerializer(doc);
    NdependQuery query = new NdependQuery("name", "group", Scope.METHOD,
        "warnif count > 0 from m in JustMyCode.Methods");
    Node node = serializer.serialize(query);
    assertThat(node.getNodeName()).isEqualTo("Query");

    String[] lines = node.getFirstChild().getTextContent().split("\n");
    assertThat(lines[0]).startsWith("// <Name>name</Name>");
    assertThat(lines[1]).startsWith(query.getCode());
    assertThat(lines[3]).startsWith("let file = m.SourceDecls.Count() > 0");
    assertThat(lines[4]).startsWith("let line = m.SourceDecls.Count() > 0");
    assertThat(lines[6]).startsWith("select new { m, file, line }");
  }

  @Test
  public void testThrowsWhenCreatingInvalidRules() throws Exception {
    Document doc = getEmptyDocument();
    QueryXmlSerializer serializer = new QueryXmlSerializer(doc);
    NdependQuery query = new NdependQuery("name", "group", Scope.METHOD,
        "warnif count > 0 from invalid_variable in JustMyCode.Methods");
    thrown.expect(IllegalArgumentException.class);
    serializer.serialize(query);
  }

  private Document getEmptyDocument() throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    return factory.newDocumentBuilder().newDocument();
  }
}
