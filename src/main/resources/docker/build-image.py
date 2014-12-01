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

print "Dockerfile located at: %s" % deployed.file.file.getCanonicalPath()

docker.build(deployed.file.file.getParentFile().getCanonicalFile().toPath(),docker.BuildParameter.FORCE_RM)
