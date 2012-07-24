#!/usr/bin/ruby
require 'rubygems'
require 'rest_client'
require 'json'
require 'pp'

DEBUG = 1

class EnterpriseServer
  attr_reader :token,:expiration

  def initialize(h = {})
    @host = 'docomo-test-es.3lm.com'
    @port = 8443

    f = open('.apipass')
    userinfo = JSON.parse(f.read)
    @user = userinfo['user']
    @pass = userinfo['pass']
    @base_url = "https://#{@host}:#{@port}/api/mt"

    if (h[:token])
      @token = h[:token]
    else
      resp = self.getSessionToken :username=> @user, :password => @pass
      @token = resp['sessionToken']
      @expiration = resp['expiration'].to_i / 1000
    end
  end

  def method_missing(m,*args)
    json = (args.size == 0)? {} : args[0]
    if @token
      json['sessionToken'] = @token
    end

    data = CGI::escape(json.to_json)
    puts "POST #{@base_url}/#{m}" if DEBUG
    RestClient.post("#{@base_url}/#{m}?","json=#{data}") do |response, request, result, &block|
      case response.code
      when 200..299
        resp = JSON.parse(URI::decode(response))
        jj resp if DEBUG
        return resp
      else
        response.return!(request,result,&block)
      end
    end
  end

  def print_expiration
    puts Time.at(es.expiration).to_s
  end

  def print_all_devices
    (self.searchForEnterprise :enterprise => {})['enterprises'].each do |ent|
      (self.getAllAcquirements :enterprise => {:domain => ent['domain']})['users'].each do |user|
        user['devices'].each do |device|
          p = device['provisioned']? '*' : ' '
          printf "%-16s%-32s #{p} %-18s %-18s\n", ent['name'], user['email'],device['mac'], device['imei']
        end
      end
    end
  end

  def print_all_policies
    (self.searchForEnterprise :enterprise => {})['enterprises'].each do |ent|
      (self.searchForPolicy :enterprise => {:domain => ent['domain']})['policies'].each do |policy|
        puts "#{policy['name']}"
        # (self.getPolicy :policy => {:id => policy['id']})['settings'].each do |setting|
        #   printf "    %-16s:%s\n", setting['key'],setting['value']
        # end
      end
    end
  end

  def print_test_policy
    (self.searchForEnterprise :enterprise => {:domain => 'test1.3lm.com'})['enterprises'].each do |ent|
      (self.searchForPolicy :enterprise => {:domain => ent['domain']})['policies'].each do |policy|
        if (policy['name'] == '3LM Test')
          puts "#{policy['name']}"
          self.updatePolicy :policy => 
            {:id => policy['id'],
             :new_settings => 
            {:uri => 'com.threelm.dm.HwAdministration',
              :key => 'corp_wifi',
              :value => "\"Wifi-None\": {\"allowedKeyManagement\": 1}"}}
          
          (self.getPolicy :policy => {:id => policy['id']})['settings'].each do |setting|
            printf "    %-16s:%s\n", setting['key'],setting['value']
          end
        end
      end
    end
  end
end

es = EnterpriseServer.new(:token => 'EicAAM3zH+e9LfwGYNHwQvWRqS8Kw33RIJ90clAvB7GKfjqKvpRvXMPiQn7Tw/yoYh0eZ++utA4Nffdc7ZdoIiLcP4TL/+D3NEmdauDOhzCy8e2w97tmKCAC439lBVgFFSdoKQ==')
puts es.print_all_devices



