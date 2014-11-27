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

/**
 * Represents a "Query" in NDepend.
 *
 * NDepend queries are extracted from exactly the same XML as the sonar 'rules'
 * (e.g. it's a different view of the same underlying object).
 */
public class NdependQuery {

  /**
   * The 'scope' of a query (e.g. what it applies to).
   */
  public enum Scope {
    FIELD,
    METHOD,
    TYPE
  }

  private final String key;

  private final String group;

  private final Scope scope;

  private final String code;

  public NdependQuery(String key, String group, Scope scope, String code) {
    this.key = key;
    this.group = group;
    this.scope = scope;
    this.code = code;
  }

  public String getKey() {
    return key;
  }

  public String getGroup() {
    return group;
  }

  public Scope getScope() {
    return scope;
  }

  public String getCode() {
    return code;
  }
}
