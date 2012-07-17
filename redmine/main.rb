#!/usr/bin/ruby

require 'rubygems'
require 'sinatra'
require 'issue'

get '/' do
  issues = Issue.find(:all, :params => {:project_id => 22, :query_id => 42, :include => 'relations'})
  ERB.new(File.new("openissues_html.erb").read,0,'>').result(binding)
end





