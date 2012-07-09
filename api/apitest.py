#!/usr/local/bin/python

import json
import urllib
import urllib2
import time

class EnterpriseServer:
    def __init__(self):
        self.host = 'docomo-jp.3lm.com'
        self.port = 8443
        self.user = 'superadmin@docomo.3lm.com'
        # XXX: Change Password
        self.pswd = '***************'
        self.base_url = "https://%s:%s/api/mt/" % (self.host,self.port)
        self.token = ''
        resp = self.post('getSessionToken',{'username': self.user, 'password': self.pswd})
        self.token = resp['sessionToken']
        
    def post(self,cmd,jd):
        if self.token:
            jd['sessionToken'] = self.token
        data = urllib.urlencode({'json':json.dumps(jd)})
        return json.loads(''.join(urllib2.urlopen(self.base_url+cmd ,data)))

es = EnterpriseServer()
for ent in (es.post('searchForEnterprise', {'enterprise': {}}))['enterprises']:
    for user in (es.post('getAllAcquirements',{'enterprise': {'domain': ent['domain']}}))['users']:
        for device in user['devices']:
            if 'mac' not in device: device['mac'] = ' '
            if 'imei' not in device: device['imei'] = ' '
            p = '*' if device['provisioned'] else ' '
            print "%-16s%-32s %s %-18s %-18s" % (ent['name'], user['email'],p,device['mac'], device['imei'])



