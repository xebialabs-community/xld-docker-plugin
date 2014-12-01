#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from com.spotify.docker.client import DefaultDockerClient, DockerCertificates
from java.net import URI
from java.nio.file import FileSystems

dockerRunner = deployed.container
serverUrl = dockerRunner.getProperty("serverUrl")
certificateLocation = dockerRunner.getProperty("certificateLocation")

print "Connecting to docker runner: %s" % dockerRunner.name

certificatePath = FileSystems.getDefault().getPath(certificateLocation)
docker = DefaultDockerClient.builder().uri(URI.create(serverUrl)).dockerCertificates(DockerCertificates(certificatePath)).build();
print "Connected to docker runner: %s" % dockerRunner.name

print "Stopping docker container %s with id: %s" % (deployed.name,deployed.containerid)
docker.stopContainer(deployed.containerid, deployed.stopTimeout);
print "Docker container %s with id: %s stopped" % (deployed.name,deployed.containerid)
