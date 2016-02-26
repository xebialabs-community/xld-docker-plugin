#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

def stop_start_docker_container(docker_container):
    if docker_container is None:
        return

    context.addStep(steps.os_script(
        description="Stopping docker container %s" % docker_container.name,
        order=20,
        script="docker/docker-stop",
        freemarker_context={'name': docker_container.name, 'target': docker_container.container},
        target_host=docker_container.container.host)
    )
    context.addStep(steps.os_script(
        description="Starting docker server %s" % docker_container.name,
        order=80,
        script="docker/docker-start",
        freemarker_context={'name': docker_container.name, 'target': docker_container.container},
        target_host=docker_container.container.host))


def containers(modify_data_volume, noop_delta_container):
    candidates = []
    for delta_v in modify_data_volume:
        for delta in noop_delta_container:
            container = delta.deployed
            if container.name == delta_v.deployedOrPrevious.containerName:
                print "Add container %s to the candidates " % container
                candidates.append(container)

    return set(candidates)


modify_data_volume = filter(lambda delta: delta.operation == "MODIFY" and (delta.deployedOrPrevious.type == "docker.DataFileVolume" or delta.deployedOrPrevious.type == "docker.DataFolderVolume"), deltas.deltas)
print "modify_data_volume %s " % modify_data_volume

noop_delta_container = filter(lambda delta: delta.operation == "NOOP" and delta.deployedOrPrevious.type == "docker.RunContainer", deltas.deltas)
print "noop_delta_container %s " % noop_delta_container

candidates = containers(modify_data_volume, noop_delta_container)
print "candidates %s " % candidates

map(stop_start_docker_container, candidates)
