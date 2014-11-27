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

import org.sonar.plugins.ndepend.NdependQuery.Scope;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Transforms Ndepend queries into XML nodes that can be written to a file.
 */
public class QueryXmlSerializer {

  private static final String DeclareFile = "let file = %s.SourceDecls.Count() > 0 ? "
      + "%s.SourceDecls.First().SourceFile.FilePath.ToStringOrIfNullToEmptyString()"
      + ": \"\"\n";

  private static final String DeclareLine = "let line = %s.SourceDecls.Count() > 0 ? "
      + "%s.SourceDecls.First().Line : 1\n";

  /**
   * A document, that we can call createElement on. Note that this code doesn't
   * actually add nodes to the document.
   */
  private final Document document;

  public QueryXmlSerializer(Document document) {
    this.document = document;
  }

  /**
   * Create a XML Query node corresponding to this query.
   */
  public Node serialize(NdependQuery query) {
    Element node = document.createElement("Query");
    node.setAttribute("Active", "True");

    // Set display list to true so that we have information about each
    // individual rule that is broken, and not just a summary.
    node.setAttribute("DisplayList", "True");

    // We don't care about the Sum, Average, etc. columns.
    node.setAttribute("DisplayStat", "True");
    node.setAttribute("IsCriticalRule", "True");
    node.appendChild(document.createCDATASection(getData(query)));
    return node;
  }

  /**
   * Use the 'code' section of the NdependQuery to write a valid query. Here, we
   * are forming a valid CQlinq query from the 'code' parameter. For this, we
   * are :
   * <ul>
   * <li>making sure that the code contains a 'from xxx' block, where xxx is
   * the name of the variable (e.g the class or method being selected)</li>
   * <li>selecting three things : the variable, the file and the line where the
   * variable is defined.</li>
   * </ul>
   */
  private String getData(NdependQuery query) {
    checkQuerySelectsVariable(query);
    StringBuilder builder = new StringBuilder();
    builder.append(String.format("// <Name>%s</Name>\n", query.getKey()));
    builder.append(query.getCode());
    builder.append("\n\n");
    String variable = getVariableName(query.getScope());
    builder.append(String.format(DeclareFile, variable, variable));
    builder.append(String.format(DeclareLine, variable, variable));
    builder.append('\n');
    builder.append(String.format("select new { %s, file, line }", variable));
    return builder.toString();
  }

  private void checkQuerySelectsVariable(NdependQuery query) {
    String pattern = String
      .format("from %s ", getVariableName(query.getScope()));
    if (!query.getCode().contains(pattern)) {
      throw new IllegalArgumentException(String.format(
        "Rule %s is invalid: it should contain '%s'", query.getKey(),
        pattern));
    }
  }

  private String getVariableName(Scope scope) {
    // TODO(m.cupcic): We could also define a <variable> element in the XML
    // so that it's customizable.
    return String.valueOf(scope.name().toLowerCase().charAt(0));
  }

}
