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
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NdependSensorTest {

  @Test
  public void testShouldExecuteOnProject() {
    Settings settings = mock(Settings.class);
    FileSystem fileSystem = mock(FileSystem.class);
    ResourcePerspectives perspectives = mock(ResourcePerspectives.class);
    ActiveRules activeRules = mock(ActiveRules.class);
    Project project = mock(Project.class);
    NdependSensor sensor = new NdependSensor(settings, fileSystem, perspectives, activeRules);
    when(fileSystem.predicates()).thenReturn(mock(FilePredicates.class));

    when(fileSystem.hasFiles(Mockito.any(FilePredicate.class))).thenReturn(false);
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    when(fileSystem.hasFiles(Mockito.any(FilePredicate.class))).thenReturn(true);
    when(activeRules.findByRepository("cs-ndepend")).thenReturn(ImmutableList.<ActiveRule>of());
    assertThat(sensor.shouldExecuteOnProject(project)).isFalse();

    when(fileSystem.hasFiles(Mockito.any(FilePredicate.class))).thenReturn(true);
    when(activeRules.findByRepository("cs-ndepend")).thenReturn(ImmutableList.of(mock(ActiveRule.class)));
    assertThat(sensor.shouldExecuteOnProject(project)).isTrue();
  }
}
