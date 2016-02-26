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




if ci_id is None:
    print "Nothing to do...."
else:
    ci = repositoryService.read(ci_id)
    print "Remove %s from %s" % (ci_member_id, ci.members)
    members = Set(ci.members)
    members.remove(repositoryService.read(ci_member_id))
    ci.members = members
    createOrUpdate(ci)


