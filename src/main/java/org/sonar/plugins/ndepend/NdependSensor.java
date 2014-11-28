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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.plugins.ndepend.ndproj.CsProjectParseError;
import org.sonar.plugins.ndepend.ndproj.NdprojCreator;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NdependSensor implements Sensor {

  private static final Logger LOG = LoggerFactory
    .getLogger(NdependSensor.class);
  private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(10);
  private final Settings settings;
  private final FileSystem fileSystem;
  private final ResourcePerspectives perspectives;

  public NdependSensor(Settings settings, FileSystem fileSystem, ResourcePerspectives perspectives) {
    this.settings = settings;
    this.fileSystem = fileSystem;
    this.perspectives = perspectives;
  }

  private File getNdProjFile(FileSystem filesystem) {
    return new File(filesystem.baseDir(), "sonar.ndproj");
  }

  @Override
  public void execute(SensorContext context) {
    LOG.debug("Executing NDepend sensor...");
    File ndprojFile = getNdProjFile(context.fileSystem());
    NdprojCreator creator = new NdprojCreator(settings, context.fileSystem());
    try {
      if (!creator.create(ndprojFile)) {
        return;
      }
    } catch (IOException e) {
      throw new IOError(e);
    } catch (CsProjectParseError e) {
      throw new RuntimeException(e);
    }

    String ndependPath = settings
      .getString(NdependConfig.NDEPEND_PATH_PROPERTY_KEY);
    try {
      Command cmd = Command.create(ndependPath)
        .addArgument(ndprojFile.getCanonicalPath())
        .addArgument("/PersistHistoricAnalysisResult");

      CommandExecutor.create().execute(cmd, TIMEOUT);
      analyzeResults(context);
    } catch (IOException e) {
      throw new IOError(e);
    }
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    LOG.debug("Describing NDepend sensor...");
    descriptor.createIssuesForRuleRepositories(NdependConfig.REPOSITORY_KEY)
      .workOnFileTypes(InputFile.Type.MAIN, InputFile.Type.TEST)
      .workOnLanguages(NdependConfig.LANGUAGE_KEY)
      .name("NDepend");
  }

  private void analyzeResults(SensorContext context) {
    List<NdependIssue> issues;
    try {
      File resultsFolder = new File(fileSystem.baseDir().getAbsolutePath(),
        NdependConfig.NDEPEND_RESULTS_FOLDER);
      File resultsFile = new File(resultsFolder,
        NdependConfig.NDEPEND_RESULTS_FILENAME);
      issues = NdependResultParser.fromFile(resultsFile).parse();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    for (NdependIssue issue : issues) {
      InputFile file;
      try {
        file = fileSystem.inputFile(fileSystem.predicates().hasAbsolutePath(issue.getFile().getCanonicalPath()));
        if (file == null) {
          LOG.error("Cannot find the following file in input files: {}", issue.getFile());
          continue;
        }
        Issuable issuable = this.perspectives.as(Issuable.class, org.sonar.api.resources.File.create(file.relativePath()));
        Issue sonarIssue = issuable.newIssueBuilder()
          .line(issue.getLine())
          .message(issue.getMessage())
          .ruleKey(RuleKey.of(NdependConfig.REPOSITORY_KEY, issue.getRuleKey()))
          .build();
        issuable.addIssue(sonarIssue);
      } catch (IOException e) {
        LOG.error("Error computing path of {}", issue.getFile(), e);
      }
    }
  }
}
