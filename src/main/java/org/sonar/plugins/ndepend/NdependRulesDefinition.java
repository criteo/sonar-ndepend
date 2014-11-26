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

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NdependRulesDefinition implements RulesDefinition {
  private static final Logger LOG = LoggerFactory
    .getLogger(NdependRulesDefinition.class);
  public static final String RULES_RESOURCE = "/org/sonar/plugins/ndepend/rules.xml";
  private final RulesDefinitionXmlLoader xmlLoader;
  private Settings settings;

  public NdependRulesDefinition(RulesDefinitionXmlLoader xmlLoader, Settings settings) {
    this.xmlLoader = xmlLoader;
    this.settings = settings;
  }

  @Override
  public void define(Context context) {
    LOG.info("Defining rules for NDepend...");
    NewRepository repository = context.createRepository(NdependConfig.REPOSITORY_KEY, NdependConfig.LANGUAGE_KEY);
    repository.setName("NDepend - C#");
    try {
      InputStream stream = new NdependRulesFetcher(settings).get();
      xmlLoader.load(repository, new InputStreamReader(stream, Charsets.UTF_8));
      repository.done();
      LOG.info("{} rules loaded.", repository.rules().size());
    } catch (IOException e) {
      LOG.error("Failed to retrieve rules: {}", e);
    }
  }
}
