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
import com.google.common.collect.ImmutableList.Builder;
import org.apache.commons.lang.StringUtils;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;
import org.codehaus.staxmate.in.SMInputCursor;
import org.sonar.plugins.ndepend.NdependQuery.Scope;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import java.io.Reader;

/**
 * Reads Ndepend queries from the rules.xml file.
 *
 * This file is largely inspired by its counterpart that loads sonar
 * rules from the same file : RulesDefinitionXmlLoader.
 */
public class QueryLoader {

  public ImmutableList<NdependQuery> getQueries(Reader reader) {
    XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
    xmlFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    xmlFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
    xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    SMInputFactory inputFactory = new SMInputFactory(xmlFactory);
    ImmutableList.Builder<NdependQuery> builder = new Builder<NdependQuery>();
    try {
      SMHierarchicCursor root = inputFactory.rootElementCursor(reader);
      root.advance();

      SMInputCursor rules = root.childElementCursor("rule");
      while (rules.getNext() != null) {
        builder.add(processRule(rules));
      }
      return builder.build();

    } catch (XMLStreamException e) {
      throw new IllegalStateException("XML is not valid", e);
    }
  }

  private NdependQuery processRule(SMInputCursor rule) throws XMLStreamException {
    String key = null;
    String group = null;
    NdependQuery.Scope scope = null;
    String code = null;

    int line = rule.getCursorLocation().getLineNumber();
    SMInputCursor cursor = rule.childElementCursor();
    while (cursor.getNext() != null) {
      String nodeName = cursor.getLocalName();

      if (StringUtils.equalsIgnoreCase("key", nodeName)) {
        key = StringUtils.trim(cursor.collectDescendantText(false));
      } else if (StringUtils.equalsIgnoreCase("group", nodeName)) {
        group = StringUtils.trim(cursor.collectDescendantText(false));
      } else if (StringUtils.equalsIgnoreCase("scope", nodeName)) {
        scope = Scope.valueOf(StringUtils.trim(cursor.collectDescendantText(false)).toUpperCase());
      } else if (StringUtils.equalsIgnoreCase("code", nodeName)) {
        code = StringUtils.trim(cursor.collectDescendantText(false));
      }
    }
    throwIfNull(key, line, "key");
    throwIfNull(scope, line, "scope");
    throwIfNull(group, line, "group");
    throwIfNull(code, line, "code");
    return new NdependQuery(key, group, scope, code);
  }

  private void throwIfNull(Object what, int line, String item) {
    if (what == null) {
      throw new AssertionError(String.format("Rule at line %d has no %s", line, item));
    }
  }
}
