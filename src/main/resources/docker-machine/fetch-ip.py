#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from overtherepy import LocalConnectionOptions, OverthereHost, OverthereHostSession
from com.xebialabs.overthere import OperatingSystemFamily
import sys
import re

def to_map(stdout):
    data={}
    for s in response.stdout:
        if 'export' in s:
            info = s.replace('export ',' ').replace('"',' ').strip().split('=')
            data[str(info[0])]=str(info[1])
    return data


session = OverthereHostSession(target.container.host)
response = session.execute("docker-machine env %s " % target.machineName)
data=to_map(response.stdout)
ip = re.findall( r'[0-9]+(?:\.[0-9]+){3}', data['DOCKER_HOST'])
if len(ip) == 1:
    data['address'] = ip[0]
    print "IP Address is %s " % data['address']
print data
session.close_conn()
context.setAttribute(context_key, data)


