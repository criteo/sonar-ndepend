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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.plugins.ndepend.NdependQuery.Scope;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class QueryLoaderTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void TestCanLoadRulesFromRulesXml() {
    InputStream stream = getClass().getResourceAsStream("/org/sonar/plugins/ndepend/rules.xml");
    QueryLoader loader = new QueryLoader();
    List<NdependQuery> queries = loader.getQueries(new InputStreamReader(stream));

    assertThat(queries.size()).isEqualTo(2);
    assertThat(queries.get(0).getScope()).isEqualTo(Scope.METHOD);
    assertThat(queries.get(0).getKey()).isEqualTo("MethodWithTooManyParameters");
    assertThat(queries.get(1).getKey()).isEqualTo("MethodTooLong");
  }

  @Test
  public void TestCanLoadValidRule() {
    QueryLoader loader = new QueryLoader();
    List<NdependQuery> queries = loader.getQueries(
      getRuleXml("<key>n</key><group>g</group><scope>method</scope><code>c</code>"));

    assertThat(queries.size()).isEqualTo(1);
    assertThat(queries.get(0).getScope()).isEqualTo(Scope.METHOD);
    assertThat(queries.get(0).getKey()).isEqualTo("n");
    assertThat(queries.get(0).getGroup()).isEqualTo("g");
  }

  @Test
  public void TestRaisesOnBadQueriesMissingCode() {
    QueryLoader loader = new QueryLoader();
    thrown.expect(AssertionError.class);

    // Missing a '<code>' element
    loader.getQueries(getRuleXml("<key>n</key><group>g</group><scope>method</scope>"));
  }

  @Test
  public void TestRaisesOnBadQueriesMissingScope() {
    QueryLoader loader = new QueryLoader();
    thrown.expect(AssertionError.class);

    // Missing a '<scope>' element
    loader.getQueries(getRuleXml("<key>n</key><group>g</group><code>c</code>"));
  }

  private Reader getRuleXml(String content) {
    return new StringReader(String.format("<rules><rule>%s</rule></rules>", content));
  }
}
