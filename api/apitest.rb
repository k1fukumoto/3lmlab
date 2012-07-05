#!/usr/bin/ruby
# -*- coding: utf-8 -*-
require 'EnterpriseServer'

es = EnterpriseServer.new()

(es.searchForEnterprise :enterprise => {})['enterprises'].each do |ent|
  (es.getAllAcquirements :enterprise => {:domain => ent['domain']})['users'].each do |user|
    user['devices'].each do |device|
      p = device['provisioned']? '*' : ' '
      printf "%-16s%-32s #{p} %-18s %-18s\n", ent['name'], user['email'],device['mac'], device['imei']
    end
  end
end

