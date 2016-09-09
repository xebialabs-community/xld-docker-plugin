# http://stackoverflow.com/questions/9845369/comparing-2-lists-consisting-of-dictionaries-with-unique-keys-in-python
from com.xebialabs.deployit.plugin.api.reflect import PropertyKind
import itertools


class CiDelta(object):

    def __init__(self, deployed, previousDeployed):
        self.deployed, self.previousDeployed = deployed, previousDeployed
        self.deployed_dict = CiDelta.to_dict(deployed)
        self.previousDeployed_dict = CiDelta.to_dict(previousDeployed)
        self.set_current, self.set_past = set(self.deployed_dict.keys()), set(self.previousDeployed_dict.keys())
        self.intersect = self.set_current.intersection(self.set_past)
        self.descriptor = metadataService.findDescriptor(Type.valueOf(str(self.deployed.type)))

    def added(self):
        return self.set_current - self.intersect

    def removed(self):
        return self.set_past - self.intersect

    def sub_ci_deltas(self):
        sub_ci_deltas = {}
        embededs_cis = [p_descriptor for p_descriptor in self.descriptor.getPropertyDescriptors() if p_descriptor.getKind() == PropertyKind.SET_OF_CI]
        for ci in embededs_cis:
            sub_deployeds = self.deployed.getProperty(ci.getName())
            sub_previous_deployeds = self.previousDeployed.getProperty(ci.getName())
            for d,p in itertools.izip_longest(sub_deployeds,sub_previous_deployeds):
                if d is None or p is None:
                    continue
                sub_delta = CiDelta(d,p)
                if ci.getName() not in sub_ci_deltas:
                    sub_ci_deltas[ci.getName()]=[]
                sub_ci_deltas[ci.getName()].append(sub_delta)
        return sub_ci_deltas

    def changed(self):
        return set(o for o in self.intersect if self.previousDeployed_dict[o] != self.deployed_dict[o])

    def is_changed(self):
        return len(self.changed()) > 0

    def unchanged(self):
        return set(o for o in self.intersect if self.previousDeployed_dict[o] == self.deployed_dict[o])

    @staticmethod
    def to_dict(ci):
        data = {'id': ci.id, 'name': ci.id.split('/')[-1]}
        for pd in ci.getType().getDescriptor().getPropertyDescriptors():
            if ci.getProperty(pd.getName()) is not None:
                data[pd.getName()] = ci.getProperty(pd.getName())
        del data['deployable']
        return data

    def delta_deployed(self):
        ci = self.descriptor.newInstance(self.deployed.id)
        ci.setProperty('container', self.deployed.getProperty('container'))
        for property_name in self.changed():
            p_descriptor = self.descriptor.getPropertyDescriptor(property_name)
            if p_descriptor.getKind() == PropertyKind.SET_OF_STRING or p_descriptor.getKind() == PropertyKind.LIST_OF_STRING:
                ci.setProperty(property_name, set(self.deployed.getProperty(property_name)) - set(self.previousDeployed.getProperty(property_name)))
            elif p_descriptor.getKind() == PropertyKind.SET_OF_CI or p_descriptor.getKind() == PropertyKind.LIST_OF_CI:
                ci.setProperty(property_name, set(self.deployed.getProperty(property_name)) - set(self.previousDeployed.getProperty(property_name)))
            else:
                ci.setProperty(property_name, self.deployed.getProperty(property_name))

        for property_name in self.added():
            ci.setProperty(property_name, self.deployed.getProperty(property_name))

        for property_name,sub_deltas in self.sub_ci_deltas().iteritems():
            for ci_delta in sub_deltas:
                if ci_delta.is_changed():
                    if not ci.hasProperty(property_name):
                        ci.setProperty(property_name,set())
                    current_value = ci.getProperty(property_name)
                    current_value.add(ci_delta.deployed)
                    ci.setProperty(property_name, current_value)
        return ci

    def delta_previous_previousDeployed(self):
        ci = self.descriptor.newInstance(self.previousDeployed.id)
        ci.setProperty('container', self.previousDeployed.getProperty('container'))
        for property_name in self.changed():
            p_descriptor = self.descriptor.getPropertyDescriptor(property_name)
            if p_descriptor.getKind() == PropertyKind.SET_OF_STRING or p_descriptor.getKind() == PropertyKind.LIST_OF_STRING:
                ci.setProperty(property_name, set(self.previousDeployed.getProperty(property_name)) - set(self.deployed.getProperty(property_name)))
            if p_descriptor.getKind() == PropertyKind.SET_OF_CI or p_descriptor.getKind() == PropertyKind.LIST_OF_CI:
                ci.setProperty(property_name, set(self.previousDeployed.getProperty(property_name)) - set(self.deployed.getProperty(property_name)))
            else:
                ci.setProperty(property_name, self.previousDeployed.getProperty(property_name))
        for property_name in self.removed():
            ci.setProperty(property_name, self.previousDeployed.getProperty(property_name))

        for property_name,sub_deltas in self.sub_ci_deltas().iteritems():
            for ci_delta in sub_deltas:
                if ci_delta.is_changed():
                    if not ci.hasProperty(property_name):
                        ci.setProperty(property_name,set())
                    current_value = ci.getProperty(property_name)
                    current_value.add(ci_delta.previousDeployed)
                    ci.setProperty(property_name, current_value)
        return ci

    def dump(self):
        print "---- DUMP %s" % self.deployed.id
        print "deployed_dict", self.deployed_dict
        print "previousDeployed_dict", self.previousDeployed_dict
        print "added", self.added()
        print "removed", self.removed()
        print "changed", self.changed()
        print "unchanged", self.unchanged()
        print "---- /DUMP %s" % self.deployed.id



delta_deployed = CiDelta(deployed,previousDeployed)
delta_deployed.dump()
freemarker_context = {}
freemarker_context['target']=deployed.container
freemarker_context['deployed']=delta_deployed.delta_deployed()
freemarker_context['previousDeployed']=delta_deployed.delta_previous_previousDeployed()


print "REPLICAS",freemarker_context['deployed'].getProperty('replicas')

context.addStep(steps.os_script(description="Update the '%s' service onto the '%s' swarm cluster" % (deployed.name, deployed.container.name),
    order=65,
    script="docker-swarm/update",
    freemarker_context=freemarker_context))


