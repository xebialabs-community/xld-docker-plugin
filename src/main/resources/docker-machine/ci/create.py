#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#


from com.xebialabs.deployit.plugin.api.reflect import Type
from sets import Set
import sys

def createOrUpdate(ci):
    if repositoryService.exists(ci.id):
        print "-- %s : updated" % ci
        return repositoryService.update(ci.id,ci)
    else:
        print "-- %s : created" % ci
        return repositoryService.create(ci.id,ci)

def newInstance(ci_type, ci_id):
    return metadataService.findDescriptor(Type.valueOf(ci_type)).newInstance(ci_id)


data = context.getAttribute(context_key)
print data

ci = newInstance(ci_type,ci_id)
ci.address = data['address']
ci.tls_verify = True if "1" in data['DOCKER_TLS_VERIFY'] else False
#ci.provider = 'virtualbox'
#ci.dynamicParameters = True
ci.certificatePath = data['DOCKER_CERT_PATH']
print ci_properties
for key, value in ci_properties.iteritems():
    print "set ci properties %s=%s" % (key,value)
    ci.setProperty(key,value)

createOrUpdate(ci)


