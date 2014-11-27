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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;

public class NdependSensorTest {

  @Test
  public void testSensorHasRightProperties() {
    SensorDescriptor descriptor = mock(DefaultSensorDescriptor.class);
    ResourcePerspectives perspectives = mock(ResourcePerspectives.class);

    when(descriptor.createIssuesForRuleRepositories("cs-ndepend")).thenReturn(descriptor);
    when(descriptor.workOnFileTypes(InputFile.Type.MAIN, InputFile.Type.TEST)).thenReturn(
        descriptor);
    when(descriptor.workOnLanguages("cs")).thenReturn(descriptor);
    new NdependSensor(new Settings(), new DefaultFileSystem(), perspectives).describe(descriptor);
    verify(descriptor).workOnLanguages("cs");
    verify(descriptor).createIssuesForRuleRepositories("cs-ndepend");
    verify(descriptor).workOnFileTypes(InputFile.Type.MAIN, InputFile.Type.TEST);
  }
}
