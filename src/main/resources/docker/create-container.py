#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from com.spotify.docker.client import DefaultDockerClient, DockerCertificates
from com.spotify.docker.client.messages import ContainerConfig
from java.net import URI
from java.nio.file import FileSystems

dockerRunner = deployed.container
serverUrl = dockerRunner.getProperty("serverUrl")
certificateLocation = dockerRunner.getProperty("certificateLocation")

print "Connecting to docker runner: %s" % dockerRunner.name

certificatePath = FileSystems.getDefault().getPath(certificateLocation)
docker = DefaultDockerClient.builder().uri(URI.create(serverUrl)).dockerCertificates(DockerCertificates(certificatePath)).build();
print "Connected to docker runner: %s" % dockerRunner.name

docker.pull(deployed.image);

print "Creating docker container: %s" % deployed.name
config = ContainerConfig.builder().image(deployed.image).cmd(deployed.cmd).build();
creation = docker.createContainer(config);

id = creation.id();
print "Created docker container %s with id: %s" % (deployed.name,id)

deployed.setProperty("containerid", id)
