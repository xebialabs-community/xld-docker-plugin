# Overview #

The Docker plugin is a XL Deploy plugin that adds capability for deploying Docker Images to Docker Machines. 
It manages also to deploy `docker-compose`files directly or to import them to turn them into `Docker.Images`.

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.
Dependencies:

* XL Deploy 4.5+ 
* xld-smoke-test plugin 1.0.2 https://github.com/xebialabs-community/xld-smoke-test-plugin/releases/tag/1.0.2


# Sample Applications #

* XL Deploy Docker Sample Application https://github.com/bmoussaud/xld-petclinic-docker
* XL Deploy Docker MicroService Sample Application https://github.com/bmoussaud/xld-micropet-docker

# Quick Start #

 

* create a local `docker-machine` named `mydemomachine` with the following command:

```
$create --driver virtualbox -  mydemomachine
Running pre-create checks...
Creating machine...
Waiting for machine to be running, this may take a few minutes...
Machine is running, waiting for SSH to be available...
Detecting operating system of created instance...
Provisioning created instance...
Copying certs to the local machine directory...
Copying certs to the remote machine...
Setting Docker configuration on the remote daemon...
To see how to connect Docker to this machine, run: docker-machine env mydemomachine
```

and run the `docker-machine env` command:

```
$docker-machine env mydemomachine
export DOCKER_TLS_VERIFY="1"
export DOCKER_HOST="tcp://192.168.99.104:2376"
export DOCKER_CERT_PATH="/Users/bmoussaud/.docker/machine/machines/mydemomachine"
```

* create an `overthere.LocalHost` : `Infrastructure/mylocalmachine`

* create a new container `docker.Machine` in the XLDeploy Repository with the following properties

  * id: Infrastructure/mylocalmachine/mydemomachine
  * Type: docker.Machine
  * address: 192.168.99.104
  * port: 2376
  * tls_verify: True
  * Certificate Path: /Users/bmoussaud/.docker/machine/machines/mydemomachine

And add it to an environment.

# Docker RunContainer Configuration #

A `docker.RunContainer` may be configured with with Embedded deployeds:
* `docker.EnvironmentVariable` to configure environment variable (name,value)
* `docker.Port` to configure the exposed ports
* `docker.Link` to configure on which other containers the container should be linked with
* `docker.Volume` to configure the volumes the container should use (hostPath:containerPath)


# Integration with a Docker Registry #

A docker registry can be set up on each `docker.RunContainer` ci using the property `Registry Host` & `Registry Port`:

* If the property is blank no registry is used. 
* If it is not blank, the images will be pulled from the Docker Registry and tagged locally.


# Usage in Deployment Packages #

Please refer to Packaging Manual for more details about the DAR packaging format.

```

<?xml version="1.0" encoding="UTF-8"?>
<udm.DeploymentPackage version="3.0-CD-20151113-172025" application="PetDocker">
  <orchestrator>
    <value>parallel-by-deployment-group</value>
  </orchestrator>
  <deployables>
    
    <smoketest.HttpRequestTest name="smoke test - ha">      
      <url>http://{{HOST_ADDRESS}}/petclinic/</url>
      <expectedResponseText>{{title}}</expectedResponseText>      
    </smoketest.HttpRequestTest>
    
    <docker.File name="petclinic.properties" file="petclinic.properties/petclinic.properties">
      <volumeName>petclinic-config</volumeName>
      <containerName>petclinic</containerName>
      <containerPath>/application/properties</containerPath>
    </docker.File>
    
    <docker.Image name="petclinic-backend">
      <image>petportal/petclinic-backend:1.1-20151311162015</image>
      <registryHost>{{PROJECT_REGISTRY_HOST}}</registryHost>
    </docker.Image>
    
    <smoketest.HttpRequestTest name="smoke test">
      <url>http://{{HOST_ADDRESS}}:{{HOST_PORT}}/petclinic/</url>
      <expectedResponseText>{{title}}</expectedResponseText>
      <links>
        <value>petclinic:petclinic</value>
      </links>
    </smoketest.HttpRequestTest>
    
    <docker.Image name="ha-proxy">
      <image>eeacms/haproxy:latest</image>
      <ports>
        <docker.PortSpec name="ha-proxy/web">
          <hostPort>80</hostPort>
          <containerPort>80</containerPort>
        </docker.PortSpec>
        <docker.PortSpec name="ha-proxy/admin">
          <hostPort>1936</hostPort>
          <containerPort>1936</containerPort>
        </docker.PortSpec>
      </ports>
      <variables>
        <docker.EnvironmentVariableSpec name="ha-proxy/BACKENDS">
          <value>{{HOST_ADDRESS}}:8888</value>
        </docker.EnvironmentVariableSpec>
      </variables>
    </docker.Image>
    
    <docker.Image name="petclinic">
      <image>petportal/petclinic:3.1-20151311162015</image>
      <volumesFrom />
      <registryHost>{{PROJECT_REGISTRY_HOST}}</registryHost>
      <ports>
        <docker.PortSpec name="petclinic/exposed-port">
          <hostPort>{{HOST_PORT}}</hostPort>
          <containerPort>8080</containerPort>
        </docker.PortSpec>
      </ports>
      <links>
        <docker.LinkSpec name="petclinic/petclinic-backend">
          <alias>petclinic-backend</alias>
        </docker.LinkSpec>
      </links>
      <volumes>
        <docker.VolumeSpec name="petclinic/petclinic-config">
          <containerPath>/application/properties</containerPath>
        </docker.VolumeSpec>
      </volumes>
      <variables>
        <docker.EnvironmentVariableSpec name="petclinic/loglevel">
          <value>{{LOGLEVEL}}</value>
        </docker.EnvironmentVariableSpec>
      </variables>
    </docker.Image>
    
  </deployables>
  <applicationDependencies />
</udm.DeploymentPackage>


```

# Deployable vs. Container Table  #

The following table describes which deployable / container combinations are possible.

| Deployables | Containers | Generated Deployed |
|-------------|------------|--------------------|
| docker.Image | docker.Machine | docker.RunContainer |
| docker.Folder | docker.Machine | docker.DataFolderVolume |
| docker.File | docker.Machine | docker.DataFileVolume |
| docker.ComposeFile | docker.Machine | docker.ComposedContainers |
| smoketest.HttpRequestTest | smoketest.DockerRunner | smoketest.ExecutedDockerizedHttpRequestTest (experimental)|
| sql.SqlScripts |sql.DockerMySqlClient | sql.DockerizedExecutedSqlScripts (experimental) |


# Deployed Actions Table  #

The following table describes the effect a deployed has on its container.

| Deployed | Create | Destroy | Modify |
|----------|--------|---------|--------|
| docker.RunContainer (*)| Pull the container image, Create the Container, Start the container | Stop the container, Remove the container | Stop the container, Remove the container, Pull the container image, Create the container, Start the container  |
| docker.DataFileVolume| Create the Docker volume, Copy the file to the volume linked to the container | Delete the file from the volume using the containers mount point | Delete the file from the volume using the containers mount point, Create the Docker volume, Copy the file to the volume linked to the container|
| docker.DataFileVolume| Create the Docker volume, Copy the folder to the volume linked to the container | Delete the folder from the volume using the containers mount point | Delete the folder from the volume using the containers mount point, Create the Docker volume, Copy the folder to the volume linked to the container|
| docker.ComposeFile| `docker-compose up`| `docker-compose stop && docker-compose rm`  | `docker-compose stop && docker-compose rm` and `docker-compose up` |


(*) the `docker.RunContainer` generates the 'create' and the 'start' steps and sorts them based on the links between the containers.

# Docker Compose File Importer #

`docker-compose`  is a great tool but it looks like a black-box. The Docker Compose file importer allows to push `docker-compose`YAML file and to turn these information into `docker.Images` defined in the plugin.


