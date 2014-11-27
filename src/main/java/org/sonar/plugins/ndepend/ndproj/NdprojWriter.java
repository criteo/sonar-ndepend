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
import java.io.Writer;
import java.util.Collection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.sonar.plugins.ndepend.NdependQuery;
import org.w3c.dom.Document;

/**
 * Writes a '.ndproj' XML file using the rules from Sonar and the information
 * extracted from the Visual Studio solutions.
 */
public class NdprojWriter {

  private SolutionInfo ndprojSolutionInfo;
  private Collection<NdependQuery> ndependQueries;
  private File outputDir;

  /**
   * Creates a writer of a '.ndproj' file.
   *
   * @param ndprojSolutionInfo
   * @param ndependQueries
   * @param outputDir
   */
  public NdprojWriter(SolutionInfo ndprojSolutionInfo,
      Collection<NdependQuery> ndependQueries, File outputDir) {
    this.ndprojSolutionInfo = ndprojSolutionInfo;
    this.ndependQueries = ndependQueries;
    this.outputDir = outputDir;
  }

  /**
   * Writes the '.ndproj' to a writer object (for example a file).
   */
  public void writeTo(Writer writer) {
    Document ndproj = buildNdprojXml();
    write(ndproj, writer);
  }

  private Document buildNdprojXml() {
    NdprojXmlBuilder xmlBuilder = new NdprojXmlBuilder();
    xmlBuilder.addOutputDir(outputDir);
    xmlBuilder.addAssemblies(this.ndprojSolutionInfo.getAssemblies());
    xmlBuilder.addFrameworkAssemblies(this.ndprojSolutionInfo
      .getFrameworkAssemblies());
    xmlBuilder.addDirs(this.ndprojSolutionInfo.getDirs());
    xmlBuilder.addQueries(this.ndependQueries);
    return xmlBuilder.getResult();
  }

  private void write(Document ndproj, Writer writer) {
    Transformer transformer;
    try {
      transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      DOMSource source = new DOMSource(ndproj);
      StreamResult result = new StreamResult(writer);
      transformer.transform(source, result);
    } catch (TransformerFactoryConfigurationError e) {
      throw new RuntimeException(e);
    } catch (TransformerException e) {
      throw new RuntimeException(e);
    }
  }
}
