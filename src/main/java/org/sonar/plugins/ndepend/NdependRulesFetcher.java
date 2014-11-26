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

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.plugins.ndepend.ndproj.NdprojCreator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NdependRulesFetcher {

  private Settings settings;
  private static final Logger LOG = LoggerFactory.getLogger(NdprojCreator.class);
  private byte[] rules;

  public NdependRulesFetcher(Settings settings) {
    this.settings = settings;
    this.rules = null;
  }

  private boolean needsFetch() {
    return rules == null;
  }

  public InputStream get() throws IOException {
    if (needsFetch()) {
      rules = fetch();
    }
    return new ByteArrayInputStream(rules);
  }

  private byte[] fetch() throws IOException {
    String rulesUrl = settings.getString(NdependConfig.NDEPEND_RULES_URL_KEY);
    LOG.info("Fetching rules from {}", rulesUrl);
    InputStream in;
    if (rulesUrl == null || rulesUrl.trim().isEmpty()) {
      LOG.info("No rules configured. Using default rules from {}.", getClass().getResource(NdependRulesDefinition.RULES_RESOURCE).toString());
      in = getClass().getResourceAsStream(NdependRulesDefinition.RULES_RESOURCE);
    } else {
      LOG.info("Loading rules from {}", rulesUrl);
      FileSystemManager vfs = VFS.getManager();
      FileObject rules = vfs.resolveFile(rulesUrl);
      in = rules.getContent().getInputStream();
    }
    return IOUtils.toByteArray(in);
  }
}
