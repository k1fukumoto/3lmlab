#!/usr/local/bin/python

import urllib
import urllib2
import json
import sys
import base64

import httplib, mimetypes, io

BOUNDARY = '439763ed5b2bac8cc359964bc1efca424751d25a27efbf50ef3d24fdd1dd21ab'
CRLF = '\r\n'


DEBUG = 1
class AuthError(Exception):
    def __init__(self,value):
        self.value = value
    def __str__(self):
        return repr(self.value)

class EnterpriseServer:
    def __init__(self):
        try:
            self.host = 'docomo-test-es.3lm.com'
            self.port = 8443
            self.user = 'superadmin@docomo_mt.3lm.com'
            self.pswd = '1rr@5hA1M@53!'
            self.base_url = "https://%s:%s/api/mt/" % (self.host,self.port)
            self.token = ''
            resp = self.post('getSessionToken',{'username': self.user, 'password': self.pswd})
            self.token = resp['sessionToken']
        except KeyError:
            raise AuthError("Failed to login Enteprise Server")

    def urlopen(self,url,jd):
        data = urllib.urlencode({'json':json.dumps(jd)})
        if DEBUG == 1:
            print ">>> %s" % url
            print json.dumps(jd,indent=4)
        resp = json.loads(urllib2.urlopen(url ,data).read())
        if DEBUG == 1:
            print "<<<"
            print json.dumps(resp,indent = 4)
        return resp
        
    def post(self,cmd,jd):
        if self.token:
            jd['sessionToken'] = self.token
        url = self.base_url+cmd
        return self.urlopen(url,jd)

    def encode_uploadApk_formdata(self,domain,name,filename):
        token = self.post('getSessionToken',
                          {'username': "%s@%s" % (self.user,domain),
                           'password': self.pswd})['sessionToken']

        form = ('--' + BOUNDARY,
                'Content-Disposition: form-data; name="sessionId"',
                '',
                token,
                '--' + BOUNDARY,
                'Content-Disposition: form-data; name="apkName"',
                '',
                name,
                '--' + BOUNDARY,
                'Content-Disposition: form-data; name="appBinaryUploadElement"; filename="%s"' % filename, 
                'Content-Type: application/octet-stream',
                '',
                open(filename).read(),
                '--' + BOUNDARY + '--')

        ios = io.BytesIO()
        for line in form:
            ios.write(str(line))
            ios.write(CRLF)
        
        return ios.getvalue()

    def uploadApk_multipart(self,domain,name,filename):
        data = self.encode_uploadApk_formdata(domain,name,filename)

        h = httplib.HTTPSConnection(self.host,8443)
        h.putrequest('POST', '/adminconsole/appUploader')
        h.putheader('content-type', 'multipart/form-data; boundary=%s' % BOUNDARY)
        h.putheader('content-length', str(len(data)))
        h.endheaders()
        h.send(data)
        return h.getresponse().read()

    def uploadApk(self,domain,name,filename):
        data = open(filename,'r').read()
        data_base64 = base64.b64encode(data)
        self.post('uploadApk',
                  {'enterprise': {'domain': domain},
                   'apk': {'contents': data_base64, 'name' : name}})
         
es = EnterpriseServer()
print es.uploadApk_multipart('test1.3lm.com','Test Apk 011',sys.argv[1])




