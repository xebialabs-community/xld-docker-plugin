#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from collections import deque

GRAY, BLACK = 0, 1

# https://gist.github.com/kachayev/5910538
def topological(graph):
    order, enter, state = deque(), set(graph), {}

    def dfs(node):
        state[node] = GRAY
        for k in graph.get(node, ()):
            sk = state.get(k, None)
            if sk == GRAY: raise ValueError("cycle")
            if sk == BLACK: continue
            enter.discard(k)
            dfs(k)
        order.appendleft(node)
        state[node] = BLACK

    while enter: dfs(enter.pop())
    return order


def to_deployed(delta):
    return delta.deployedOrPrevious


def deployeds(candidate_filter):
    return set(map(to_deployed, filter(candidate_filter, deltas.deltas)))


def to_delta(deployed):
    deployed_deltas = filter(lambda delta: delta.deployedOrPrevious == deployed, deltas.deltas)
    if len(deployed_deltas) == 1:
        return deployed_deltas[0]
    else:
        raise Exception("Too many deltas %s for this deployed %s " % (deployed, deployed_deltas))


def create_docker_container(deployed):
    context.addStepWithCheckpoint(steps.os_script(
        description="Create the container '%s' (%s)" % (deployed.name, deployed.image),
        order=65,
        script="docker/docker-create",
        freemarker_context={'name': deployed.name, 'target': deployed.container, "deployed_container":deployed},
        target_host=deployed.container.host), to_delta(deployed))


def start_docker_container(deployed):
    context.addStep(steps.os_script(
        description="Start the container '%s' (%s)" % (deployed.name, deployed.image),
        order=80,
        script="docker/docker-start",
        freemarker_context={'name': deployed.name, 'target': deployed.container},
        target_host=deployed.container.host))


def sort_containers(docker_run_containers):
    graph = {}
    for d in docker_run_containers:
        graph[d.name] = map(lambda a: a.name, d.links)
        if d.dependencies is not None:
            for dep in d.dependencies:
                if dep not in graph[d.name]:
                    graph[d.name].append(dep)

    print "Container graph: %s " % graph

    sorted_docker_run_containers = []
    for x in reversed(topological(graph)):
        sorted_docker_run_containers.append(filter(lambda drc: drc.name == x, docker_run_containers)[0])
    return sorted_docker_run_containers


docker_run_containers = deployeds(
    lambda delta: (delta.operation == "CREATE" or delta.operation == "MODIFY") and delta.deployedOrPrevious.type == "docker.RunContainer")

if len(docker_run_containers) > 0:
    sorted_docker_run_containers = sort_containers(docker_run_containers)
    print "Sorted Docker Containers: %s " % sorted_docker_run_containers

    map(create_docker_container, sorted_docker_run_containers)
    map(start_docker_container, sorted_docker_run_containers)
