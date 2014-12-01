#
# THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
# FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
#

from com.spotify.docker.client import DockerClient, DefaultDockerClient, DockerCertificates
from java.net import URI
from java.nio.file import FileSystems

dockerRunner = thisCi.container
serverUrl = dockerRunner.getProperty("serverUrl")
certificateLocation = dockerRunner.getProperty("certificateLocation")

print "Connecting to docker runner: %s" % dockerRunner.name

certificatePath = FileSystems.getDefault().getPath(certificateLocation)
docker = DefaultDockerClient.builder().uri(URI.create(serverUrl)).dockerCertificates(DockerCertificates(certificatePath)).build();
print "Connected to docker runner: %s" % dockerRunner.name

containerId = thisCi.getProperty("containerid")
follow = parameters["follow"]
stdout = parameters["stdout"]
stderr = parameters["stderr"]
timestamps = parameters["timestamps"]
tail = parameters["tail"]

logParams = []
if follow:
    logParams.append(DockerClient.LogsParameter.FOLLOW)
if stdout:
    logParams.append(DockerClient.LogsParameter.STDOUT)
if stderr:
    logParams.append(DockerClient.LogsParameter.STDERR)
if timestamps:
    logParams.append(DockerClient.LogsParameter.TIMESTAMPS)

logStream = docker.logs(containerId, logParams)

print "Found logs: %s" % logStream.readFully()
