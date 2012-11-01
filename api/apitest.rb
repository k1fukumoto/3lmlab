#!/usr/bin/ruby
require 'rubygems'
require 'rest_client'
require 'json'
require 'pp'
require 'hexdump'

DEBUG = 1

class EnterpriseServer
  attr_reader :token,:expiration

  def initialize(h = {})
    @host = h[:host]
    @port = 8443

    f = open('.apipass')
    userinfo = JSON.parse(f.read)[@host]
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
    jj json if DEBUG
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

  def print_device(user)
    (self.searchForUser :user => {:email => user})['users'].each do |user|
      user['devices'].each do |dev|
        (self.searchForDevice :device => {:id => dev['id']})['device']['settings'].sort {|a,b| a['key'] <=> b['key']}.each do |s|
          if s['key'] == 'wifi_provision_list'
            puts s['value']
            s['value'].hexdump
          end
          
#          printf "%-32s%-16s%s\n", s['key'],s['value'],s['complianceState']
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

  def update_policy(dom,pol)
    (self.searchForPolicy :enterprise => {:domain => dom}, :policy => {:searchTerm => pol})['policies'].each do |policy|
      puts "#{policy['name']}"
      self.updatePolicy :policy => {
        :id => policy['id'],
        :new_settings => [{ :key => 'apn_provision_list',
                            :value => "{'apnArr':[{'apn':'123','authtype':'NONE','bearer':'UNSPECIFIED','mcc':'123','mmsc':'mmsc','mmsport':'123','mmsproxy':'mmsproxy','mnc':'123','name':'foo','password':'password','port':'123','protocol':'IPV6','proxy':'proxy','roaming_protocol':'IP','server':'server','type':'type','user':'user'}]}"}]
      }
    end
  end

  def get_policy(dom,pol)
    (self.searchForPolicy :enterprise => {:domain => dom}, :policy => {:searchTerm => pol})['policies'].each do |policy|
      puts "#{policy['name']}"
      self.getPolicy :policy => {:id => policy['id']}
    end
  end

end

es = EnterpriseServer.new(:host => 'docomo-test-es.3lm.com')
#es.update_policy('test1.3lm.com','3LM API Test')

# es.registerAdmin :user => {:email => 'threelm@test1.3lm.com', :password => 'threelm'}
#es.searchForPolicy :enterprise => {:domain => 'test1.3lm.com'}
#es.removePolicy :policy => {:id => '10006:999'}

# (es.searchForUser :user => {:email => 'k@test1.3lm.com' })['users'].each do |user|
#   user['devices'].each do |dev|
#     es.pushCommandsToDevice :device => {:id => dev['id']}, :commands => [{:key => 'lockout', :value => '1111'}]
#   end
# end
es.print_device('k@test1.3lm.com')


