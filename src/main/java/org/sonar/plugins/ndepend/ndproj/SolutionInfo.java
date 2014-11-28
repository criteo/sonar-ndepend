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

import java.util.Collection;

/**
 * Information extracted from a DotNet solution and necessary to create an
 * '.ndproj' file.
 */
class SolutionInfo {
  private Collection<String> assemblies;
  private Collection<String> frameworkAssemblies;
  private Collection<String> dirs;

  /**
   * Constructor.
   *
   * @param assemblies
   *          names of assembly to analyse
   * @param frameworkAssemblies
   *          names of dependencies
   * @param dirs
   *          directories where are located the assemblies
   */
  public SolutionInfo(Collection<String> assemblies,
    Collection<String> frameworkAssemblies, Collection<String> dirs) {
    this.assemblies = assemblies;
    this.frameworkAssemblies = frameworkAssemblies;
    this.dirs = dirs;
  }

  /**
   * @return names of assembly to analyse
   */
  public Collection<String> getAssemblies() {
    return this.assemblies;
  }

  /**
   * @return names of dependencies
   */
  public Collection<String> getFrameworkAssemblies() {
    return this.frameworkAssemblies;
  }

  /**
   * @return directories where are located the assemblies
   */
  public Collection<String> getDirs() {
    return this.dirs;
  }
}
