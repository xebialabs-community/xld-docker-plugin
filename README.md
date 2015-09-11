# Preface #

This document describes the functionality provided by the Docker plugin.

See the **XL Deploy Reference Manual** for background information on XL Deploy and deployment concepts.

# Overview #

The Docker plugin is a XL Deploy plugin that adds capability for deploying applications to a Docker container.

# Requirements #

* **Requirements**
	* **XL Deploy** 5.0.2+

# Installation #

Place the plugin `xldp` file into your `SERVER_HOME/plugins` directory.

# Usage #

1. Go to `Repository - Infrastructure`, create a new `docker.Runner`.
    * `serverUrl`: Example: https://boot2docker:2376
    * `certificateLocation`: The semantics are similar to using the DOCKER_CERT_PATH environment variable: https://docs.docker.com/articles/https/#client-modes
2. Create an environment under `Repository - Environments`
3. Create an application with `docker.Build` and `docker.Container` as deployables.
4. Start deploying
