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
import org.sonar.api.config.Settings;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NdependRulesDefinitionTest {

  @Test
  public void test() {
    RulesDefinition.Context context = new RulesDefinition.Context();
    Settings settings = mock(Settings.class);
    NdependRulesDefinition definition = new NdependRulesDefinition(new RulesDefinitionXmlLoader(), settings);

    when(settings.getString(NdependConfig.NDEPEND_RULES_URL_KEY)).thenReturn(" ");

    definition.define(context);
    Repository repo = context.repository(NdependConfig.REPOSITORY_KEY);

    List<RulesDefinition.Rule> rules = repo.rules();
    assertThat(rules.size()).isEqualTo(2);
    for (RulesDefinition.Rule rule : rules) {
      assertThat(rule.key()).isNotNull();
      assertThat(rule.name()).isNotNull();
      assertThat(rule.htmlDescription()).isNotNull();
    }
  }
}
