# Preface #

This document describes the functionality provided by the Docker plugin.

See the **XL Deploy Reference Manual** for background information on XL Deploy and deployment concepts.

# Overview #

The Docker plugin is a XL Deploy plugin that adds capability for deploying applications to a Docker container.

# Requirements #

* **Requirements**
	* **XL Deploy** 4.5.0
	* **Additional Runtime Libraries**
		* com.spotify:docker-client:2.7.1
        * org.apache.httpcomponents:httpclient:4.3.5 (into lib, remove older version)
        * org.apache.httpcomponents:httpcore:4.3.2  (into lib, remove older version)
        * org.apache.commons:commons-compress:1.8.1  (into lib, remove older version)
        * hotfix-DEPL-6955.jar (into hotfix)

# Installation #

Place the plugin JAR file into your `SERVER_HOME/plugins` directory.   Make sure you have additional runtime libiraries mentioned in the requirements section also installed in the correct directory.

# Usage #

1. Go to `Repository - Infrastructure`, create a new `docker.Runner`.
    * `serverUrl`: Example: https://boot2docker:2376
    * `certificateLocation`: The semantics are similar to using the DOCKER_CERT_PATH environment variable: https://docs.docker.com/articles/https/#client-modes
2. Create an environment under `Repository - Environments`
3. Create an application with `docker.Build` and `docker.Container` as deployables.
4. Start deploying
