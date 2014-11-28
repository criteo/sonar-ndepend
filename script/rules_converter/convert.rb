#!/usr/bin/env ruby
# Convert rules from a typical .ndproj generated with NDepend to queries for Sonar
# Usage:
#   convert.rb MyProject.ndproj rules.xml

require 'fileutils'
require 'nokogiri'
require 'erb'

class Rule
  attr_reader :name, :key, :severity, :group, :scope, :code, :description

  def initialize(name, key, severity, group, scope, code, description)
    @name = name
    @key = key
    @severity = severity
    @group = group
    @scope = scope
    @code = code
    @description = description
  end
end

class RulesTemplate
  def initialize(rules)
    @template_file = 'rules.xml.erb'
    @rules = rules
  end

  # Return a string containing the computed file.
  def build
    template_file = File.join(File.dirname(__FILE__), @template_file)
    erb = ERB.new(File.read(template_file))
    erb.result binding
  end
end

def query2rule(query)
  content = query.content
  name = content[/.*<Name>(.*)<\/Name>.*/, 1]
  raise "No name found. Note we don't parse <TrendMetric Name=\"...\" /> as we want only boolean rules." unless name

  key = name.split.map(&:capitalize).join(' ').tr('^A-Za-z', "")

  severity = query['IsCriticalRule'] == "True" ? "CRITICAL" : "MINOR"

  group = query.parent['Name']

  scope_path = content[/from [a-zA-Z]+ in ([A-Za-z\.]*)/, 1]
  raise "No scope found." unless scope_path
  split_scope_path = scope_path.split(".") - ['Application', 'JustMyCode', 'ThirdParty']
  raise "No scope found." if split_scope_path.empty?
  scope = split_scope_path.first.sub(/s$/, "")
  raise "Ignored scope: #{scope}" unless ['Type', 'Method'].include? scope

  raise "Invalid 'from x' clause" unless content[/from #{scope.downcase[0]} /]

  # Remove 'Name' tag as it will be added by the sonar-ndepend plugin.
  code = content.gsub(/^.*<\/Name>(.*)/m, '\1') \
    # Remove 'select' as it will be added by the sonar-ndepend plugin.
    .gsub(/(.*)select .*/m, '\1')

  description = name # Maybe we could parse all lines prefixed by "//" instead

  return Rule.new(name, key, severity, group, scope, code, description)
end

def convert(ndproj_path, rules_path)
  ndproj_file = File.open(ndproj_path)
  doc = Nokogiri::XML(ndproj_file)
  rules = []
  total = 0
  doc.xpath("//Query[@Active='True']").each do |query|
    total += 1
    begin
      rule = query2rule(query)
      rules << rule
      puts "[OK]    Converted: #{rule.name}"
    rescue Exception => e
      puts "[ERROR] #{e}"
      #puts query.to_xml
    end
  end
  puts "Converted #{rules.size} among #{total}"

  template = RulesTemplate.new(rules)
  file_content = template.build()
  # Remove trailing whitespaces
  file_content.gsub!(/\s+$/, '')
  File.open(rules_path, 'w') do |file|
    file.write(file_content)
  end
end

if __FILE__ == $0
  convert(ARGV[0], ARGV[1])
end
