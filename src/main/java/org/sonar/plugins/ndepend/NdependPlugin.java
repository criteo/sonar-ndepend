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

import com.google.common.collect.ImmutableList;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public class NdependPlugin extends SonarPlugin {

  private static final String CATEGORY = "Ndepend";

  /**
   * {@inheritDoc}
   */
  @Override
  public List getExtensions() {
    ImmutableList.Builder builder = ImmutableList.builder();
    builder.addAll(NdependProvider.extensions());
    builder.addAll(pluginProperties());
    return builder.build();
  }

  private static ImmutableList<PropertyDefinition> pluginProperties() {
    return ImmutableList.of(
      PropertyDefinition.builder(NdependConfig.SOLUTION_PATH_PROPERTY_KEY)
      .name("Path to the .sln file")
      .description("Example: myproject.sln")
      .category(CATEGORY)
      .onlyOnQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
      .build(),

      PropertyDefinition.builder(NdependConfig.NDEPEND_PATH_PROPERTY_KEY)
      .name("Path to NDepend.Console.exe")
      .description("Example: C:/ndepend/NDepend.Console.exe")
      .defaultValue("C:/tools/ndepend/NDepend.Console.exe")
      .category(CATEGORY)
      .onQualifiers(Qualifiers.PROJECT)
      .build(),

      PropertyDefinition.builder(NdependConfig.NDEPEND_RULES_PATH_KEY)
      .name("Path to a rules file")
      .description("Eg. /path/to/rules.xml")
      .category(CATEGORY)
      .onlyOnQualifiers(Qualifiers.PROJECT)
      .build()
      );
  }
}
