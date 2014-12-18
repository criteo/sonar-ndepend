sonar-ndepend
=============

[![Build Status](https://travis-ci.org/criteo/sonar-ndepend.svg?branch=master)](https://travis-ci.org/criteo/sonar-ndepend)

A [SonarQube](http://www.sonarqube.org/) plugin that reports
[NDepend](http://www.ndepend.com/) issues to Sonar.

This plugin parses a `.sln` file to generate a `.ndproj` file. This
file is passed to `NDepend.Console.exe`. The result file
`CodeRuleResult.xml` is then parsed and the issues are uploaded to the
Sonar server.


Configuration
-------------

The following parameter must be configured in `sonar-project.properties`:

* `sonar.ndepend.solutionFile`: path to the `.sln` file.

The following parameters must be configured in `sonar-runner.properties`:

* `sonar.ndepend.ndependPath`: path to `NDepend.Console.exe`.
* `sonar.ndepend.rulesPath`: _(optional)_ a path to a rules definition file.
[RulesDefinitionXmlLoader](http://docs.sonarsource.org/latest/apidocs/org/sonar/api/server/rule/RulesDefinitionXmlLoader.html)
is used to parse the XML.

Rules
-----

The plugin comes with some [default
rules](src/main/resources/org/sonar/plugins/ndepend/rules.xml).  These
are more or less all the default NDepend rules on the _Types_ or
_Methods_ domains, and without the `select` block in the CQLink query.
The plugin automatically appends a `"select new {variable, file,
line}"` block to the CQLink query where `variable` comes from the
`"from variable"` block.

You may use [convert.rb](script/rules_converter/convert.rb) to
generate your own rules file from an existing `.ndproj` file.

You can then replace the default rules using the
`sonar.ndepend.rulesPath` parameter.

When the plugin is loaded in the Sonar server, the rules are added to
the 'NDepend - C#' repository.
