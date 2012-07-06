require 'rubygems'
require 'rest_client'
require 'json'
require 'pp'

class EnterpriseServer
  attr_reader :token,:expiration

  def initialize
    @host = 'docomo-jp.3lm.com'
    @port = 8443
    @user = 'superadmin@docomo.3lm.com'
    @pass = 'bf1C2p54o72VY8P8LSOV'
    @base_url = "https://#{@host}:#{@port}/api/mt"

    resp = self.getSessionToken :username=> @user, :password => @pass
    @token = resp['sessionToken']
    @expiration = resp['expiration'].to_i / 1000
  end

  def method_missing(m,*args)
    json = (args.size == 0)? {} : args[0]
    if @token
      json['sessionToken'] = @token
    end

    data = CGI::escape(json.to_json)
#    puts "POST #{@base_url}/#{m}"
    RestClient.post("#{@base_url}/#{m}?","json=#{data}") do |response, request, result, &block|
      case response.code
      when 200..299
        resp = JSON.parse(URI::decode(response))
#        puts "<< " + resp.to_json
        return resp
      else
        response.return!(request,result,&block)
      end
    end
  end
end
