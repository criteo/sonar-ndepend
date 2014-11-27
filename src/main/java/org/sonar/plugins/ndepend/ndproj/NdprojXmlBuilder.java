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

import java.io.File;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sonar.plugins.ndepend.NdependQuery;
import org.sonar.plugins.ndepend.QueryXmlSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Builder of an XML Document representing a '.ndproj' file.
 * <p>
 * '.ndproj' files are XML files describes what must be analysed by NDepend.
 * There are used by "ndepend-console.exe".
 */
class NdprojXmlBuilder {
  private Document doc;
  private Element root;

  /**
   * Constructor.
   */
  public NdprojXmlBuilder() {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try {
      builder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
    this.doc = builder.newDocument();
    this.root = this.doc.createElement("NDepend");
    this.root.setAttribute("AppName", "default");
    this.doc.appendChild(this.root);
  }

  public void addOutputDir(File outputDir) {
    Element outputDirElement = this.doc.createElement("OutputDir");
    outputDirElement.setAttribute("KeepXmlFiles", "True");
    outputDirElement.setTextContent(outputDir.getAbsolutePath());
    this.root.appendChild(outputDirElement);
  }

  public void addAssemblies(Collection<String> assemblyNames) {
    addAssemblies("Assemblies", assemblyNames);
  }

  public void addFrameworkAssemblies(Collection<String> assemblyNames) {
    addAssemblies("FrameworkAssemblies", assemblyNames);
  }

  public void addDirs(Collection<String> dirs) {
    Element assemblies = this.doc.createElement("Dirs");
    for (String dirPath : dirs) {
      Element dir = this.doc.createElement("Dir");
      dir.setTextContent(dirPath);
      assemblies.appendChild(dir);
    }
    this.root.appendChild(assemblies);
  }

  public void addQueries(Collection<NdependQuery> ndependQueries) {
    Element queries = this.doc.createElement("Queries");
    for (NdependQuery ndependQuery : ndependQueries) {
      QueryXmlSerializer querySerializer = new QueryXmlSerializer(this.doc);
      Node queryNode = querySerializer.serialize(ndependQuery);
      queries.appendChild(queryNode);
    }
    this.root.appendChild(queries);
  }

  public Document getResult() {
    return this.doc;
  }

  private void addAssemblies(String AssemblyKind,
      Collection<String> assemblyNames) {
    Element assemblies = this.doc.createElement(AssemblyKind);
    for (String assemblyName : assemblyNames) {
      Element name = this.doc.createElement("Name");
      name.setTextContent(assemblyName);
      assemblies.appendChild(name);
    }
    this.root.appendChild(assemblies);
  }
}
