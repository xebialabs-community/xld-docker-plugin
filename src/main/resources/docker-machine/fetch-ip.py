#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from overtherepy import OverthereHostSession
import re
import sys


def to_map(stdout):
    _data = {}
    for s in response.stdout:
        if 'export' in s:
            info = s.replace('export ', ' ').replace('"', ' ').strip().split('=')
            _data[str(info[0])] = str(info[1]).strip()
    return _data


target_host = target.container.host
session = OverthereHostSession(target_host)
response = session.execute("docker-machine env %s " % target.machineName)
data = to_map(response.stdout)
ip = re.findall(r'[0-9]+(?:\.[0-9]+){3}', data['DOCKER_HOST'])
if len(ip) == 1:
    data['address'] = ip[0]
    print "IP Address is %s " % data['address']

print "--------------------"
print data
print "--------------------"

session.close_conn()

deployed.docker_host_address = data['address']
deployed.docker_host = data['DOCKER_HOST']
deployed.docker_cert_path = data['DOCKER_CERT_PATH']
deployed.docker_tls_verify = data['DOCKER_TLS_VERIFY']

print "done"

