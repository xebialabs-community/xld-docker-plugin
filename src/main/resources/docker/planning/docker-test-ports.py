#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#


def to_deployed(delta):
    return delta.deployedOrPrevious


def deployeds(candidate_filter):
    return set(map(to_deployed, filter(candidate_filter, deltas.deltas)))


def test_ports_on_containers(deployed):
    for port in deployed.ports:
        if port.testPort:
            context.addStep(steps.os_script(
                description="Test the '%s' port on container '%s'" % (port.name, deployed.name),
                order=101,
                script="docker/docker-port",
                freemarker_context={'name': deployed.name, 'target': deployed.container, 'port': port},
                target_host=deployed.container.host))


docker_run_containers = deployeds(
    lambda delta: (delta.operation == "CREATE" or delta.operation == "MODIFY") and delta.deployedOrPrevious.type == "docker.RunContainer")

map(test_ports_on_containers, docker_run_containers)
