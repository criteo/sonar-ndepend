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

import com.google.common.base.Function;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.File;
import java.util.HashSet;

public class CsProjectParser {

  private static String ASSEMBLY_NAME_XPATH = "/Project/PropertyGroup/AssemblyName";
  private static String OUTPUT_XPATH = "//OutputPath";
  private static String DEPENDENCIES_XPATH = "//Reference/@Include";

  private final XPath xpath;
  private XPathExpression assemblyNameXpath;
  private XPathExpression outputPathXpath;
  private XPathExpression dependenciesXpath;

  private static final Function<Node, String> nodeAttr = new Function<Node, String>() {
    @Override
    public String apply(Node n) {
      return ((Attr) n).getValue();
    }
  };

  private static final Function<Node, String> nodeValue = new Function<Node, String>() {
    @Override
    public String apply(Node n) {
      return ((Element) n).getTextContent();
    }
  };

  private static final Function<Node, String> referenceValue = new Function<Node, String>() {
    @Override
    public String apply(Node n) {
      String[] ref = nodeAttr.apply(n).split(",");
      return ref[0];
    }
  };

  CsProjectParser() {
    xpath = XPathFactory.newInstance().newXPath();
    try {
      assemblyNameXpath = xpath.compile(ASSEMBLY_NAME_XPATH);
      outputPathXpath = xpath.compile(OUTPUT_XPATH);
      dependenciesXpath = xpath.compile(DEPENDENCIES_XPATH);
    } catch (XPathExpressionException e) {
      throw new RuntimeException("Failed to build xpath expressions");
    }
  }

  private HashSet<String> mapNodes(NodeList nodes, Function<Node, String> f) {
    HashSet<String> set = new HashSet<String>();
    for (int i = 0; i < nodes.getLength(); i++) {
      set.add(f.apply(nodes.item(i)));
    }
    return set;
  }

  public CsProjectInfo parse(File projectFile) throws CsProjectParseError {
    try {
      final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(projectFile.getCanonicalPath());
      String assembly = nodeValue.apply((Node) assemblyNameXpath.evaluate(doc, XPathConstants.NODE));
      HashSet<String> outputPaths = new HashSet<String>();
      for (String path : mapNodes((NodeList) outputPathXpath.evaluate(doc, XPathConstants.NODESET), nodeValue)) {
        File outputFile = new File(projectFile.getParentFile().getCanonicalPath(), path);
        outputPaths.add(outputFile.getCanonicalPath());
      }
      HashSet<String> deps = mapNodes((NodeList) dependenciesXpath.evaluate(doc, XPathConstants.NODESET), referenceValue);
      return new CsProjectInfo(assembly, outputPaths, deps);
    } catch (Exception e) {
      throw new CsProjectParseError(String.format("Failed to parse CS project (%s): %s", projectFile.getPath(), e.getMessage()));
    }
  }
}
