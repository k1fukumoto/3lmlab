#!/usr/local/bin/python

import json
import urllib
import urllib2
import time

class EnterpriseServer:
    def __init__(self,cmd):
        self.host = 'docomo-jp.3lm.com'
        self.port = 8443
        self.user = 'superadmin@docomo.3lm.com'
        self.pswd = 'bf1C2p54o72VY8P8LSOV'
        self.base_url = "https://%s:%s/api/mt/" % (self.host,self.port)
        
        resp = self.post('getSessionToken',{'username': self.user, 'password': self.pswd})
        self.token = resp['sessionToken']
        
    def post(self,cmd,jd):
        data = urllib.urlencode({'json':json.dumps(jd)})
        return json.loads(''.join(urllib2.urlopen(self.base_url+cmd ,data)))

enterprises = URL('searchForEnterprise').post({
        'sessionToken': token
        'enterprise': {}
    })

print enterprises

# for e in enterprises:
#    print '%s (%s) %d' % (e['name'],e['domain'],e['maxActivations'])

