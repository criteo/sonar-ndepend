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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.plugins.ndepend.NdependConfig;
import org.sonar.plugins.ndepend.NdependQuery;
import org.sonar.plugins.ndepend.NdependRulesFetcher;
import org.sonar.plugins.ndepend.QueryLoader;
import org.sonar.plugins.ndepend.SlnParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;

/**
 * Creator of '.ndproj' files.
 */
public class NdprojCreator {
  private final Settings settings;
  private FileSystem filesystem;
  private static final Logger LOG = LoggerFactory.getLogger(NdprojCreator.class);

  /**
   * Constructor.
   *
   * @param settings
   */

  public NdprojCreator(Settings settings, FileSystem fs) {
    this.settings = settings;
    this.filesystem = fs;
  }

  /**
   * Create a '.ndproj' file by parsing the '.csproj' files linked to a '.sln'
   * file.
   *
   * @param ndprojFile
   *          the file to generate
   */
  public boolean create(File ndprojFile) throws IOException,
    CsProjectParseError {
    File solutionFile = new File(
      settings.getString(NdependConfig.SOLUTION_PATH_PROPERTY_KEY));
    NdprojInfo ndprojSolutionInfo = readProjectInfo(solutionFile);
    Collection<NdependQuery> ndependQueries = readQueries();
    if (ndprojSolutionInfo.getAssemblies().isEmpty()) {
      return false;
    }
    File outputDir = new File(filesystem.baseDir().getAbsolutePath(),
      NdependConfig.NDEPEND_RESULTS_FOLDER);
    NdprojWriter ndprojWriter = new NdprojWriter(ndprojSolutionInfo,
      ndependQueries, outputDir);
    Writer writer = new FileWriter(ndprojFile);
    ndprojWriter.writeTo(writer);
    writer.close();
    return true;
  }

  private Collection<NdependQuery> readQueries() throws IOException {
    InputStream in = new NdependRulesFetcher(settings).get();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    QueryLoader queryLoader = new QueryLoader();
    return queryLoader.getQueries(reader);
  }

  private NdprojInfo readProjectInfo(File solutionFile)
    throws CsProjectParseError, IOException {
    SlnParser slnParser = new SlnParser();
    Collection<File> csprojs = slnParser.parse(solutionFile);
    HashSet<String> assemblyNames = new HashSet<String>();
    HashSet<String> dependencies = new HashSet<String>();
    HashSet<String> outputPaths = new HashSet<String>();
    for (File csprojFile : csprojs) {
      if (this.filesystem.baseDir().getCanonicalPath().equals(csprojFile.getParentFile().getCanonicalPath())) {
        CsProjectParser csProjectParser = new CsProjectParser();
        CsProjectInfo csProjectInfo = csProjectParser.parse(csprojFile);
        assemblyNames.add(csProjectInfo.getAssemblyName());
        dependencies.addAll(csProjectInfo.getReferences());
        outputPaths.addAll(csProjectInfo.getOutputPaths());
      }
    }
    return new NdprojInfo(assemblyNames, dependencies, outputPaths);
  }
}
