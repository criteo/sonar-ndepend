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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.plugins.ndepend.NdependConfig;
import org.sonar.plugins.ndepend.NdependQuery;
import org.sonar.plugins.ndepend.NdependRulesDefinition;
import org.sonar.plugins.ndepend.QueryLoader;
import org.sonar.plugins.ndepend.SlnParser;

/**
 * Creator of '.ndproj' files.
 */
public class NdprojCreator {
  private final Settings settings;
  private static final Logger LOG = LoggerFactory.getLogger(NdprojCreator.class);

  /**
   * Constructor.
   *
   * @param settings
   */
  public NdprojCreator(Settings settings) {
    this.settings = settings;
  }

  /**
   * @return an InputStream for the loaded rules file
   * @throws FileSystemException
   */
  private InputStream getRulesInputStream() throws FileSystemException {
    String rulesUrl = settings.getString(NdependConfig.NDEPEND_RULES_URL_KEY);
    InputStream in;
    if (rulesUrl.trim().isEmpty()) {
      LOG.info("No rules configured. Using default rules");
      in = getClass()
          .getResourceAsStream(NdependRulesDefinition.RULES_RESOURCE);
    } else {
      LOG.info("Loading rules from {}", rulesUrl);
      FileSystemManager vfs = VFS.getManager();
      FileObject rules = vfs.resolveFile(rulesUrl);
      in = rules.getContent().getInputStream();
    }
    return in;
  }

  /**
   * Create a '.ndproj' file by parsing the '.csproj' files linked to a '.sln'
   * file.
   *
   * @param ndprojFile
   *          the file to generate
   */
  public void create(File ndprojFile) throws IOException,
  CsProjectParseError {
    File solutionFile = new File(
      settings.getString(NdependConfig.SOLUTION_PATH_PROPERTY_KEY));
    SolutionInfo ndprojSolutionInfo = readSolutionInfo(solutionFile);
    Collection<NdependQuery> ndependQueries = readQueries();
    NdprojWriter ndprojWriter = new NdprojWriter(ndprojSolutionInfo,
      ndependQueries);
    Writer writer = new FileWriter(ndprojFile);
    ndprojWriter.writeTo(writer);
  }

  private Collection<NdependQuery> readQueries() throws IOException {

    InputStream in;
    try {
      in = getRulesInputStream();
    } catch (FileSystemException e) {
      LOG.error("Failed to retrieve rules file: {}", e);
      throw new IOException(e);
    }
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    QueryLoader queryLoader = new QueryLoader();
    return queryLoader.getQueries(reader);
  }

  private static SolutionInfo readSolutionInfo(File solutionFile)
    throws CsProjectParseError {
    SlnParser slnParser = new SlnParser();
    Collection<File> csprojs = slnParser.parse(solutionFile);
    HashSet<String> assemblyNames = new HashSet<String>();
    HashSet<String> dependencies = new HashSet<String>();
    HashSet<String> outputPaths = new HashSet<String>();
    for (File csprojFile : csprojs) {
      CsProjectParser csProjectParser = new CsProjectParser();
      CsProjectInfo csProjectInfo = csProjectParser.parse(csprojFile);
      assemblyNames.add(csProjectInfo.getAssemblyName());
      dependencies.addAll(csProjectInfo.getReferences());
      outputPaths.addAll(csProjectInfo.getOutputPaths());
    }
    return new SolutionInfo(assemblyNames, dependencies, outputPaths);
  }
}
