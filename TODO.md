# TODO #

* Delete data in the volume (DESTROY)
* XL Deploy plugins packaged as docker images (xld smoke test plugin as a container).
* Docker Swarm.
    - Manage filter & affinity : https://docs.docker.com/swarm/scheduler/filter/
* Decide if the container_name should contains the application name (like in docker-compose)
* Manage Published ports:  https://docs.docker.com/engine/userguide/networking/default_network/dockerlinks/
* Support Docker network http://www.javacodegeeks.com/2015/11/deploying-containers-docker-swarm-docker-networking.html
    $ docker create network mynetwork
    Error: Error response from daemon: 500 Internal Server Error: failed to parse pool request for address space "GlobalDefault" pool "" subpool "": cannot find address space GlobalDefault (most likely the backing datastore is not configured)
    - https://github.com/docker/docker/issues/17932
    - https://gist.github.com/cpuguy83/79ad11aaf8e78c40ca71
    
* Multi Docker Machine & Consul & networking
- https://developer.atlassian.com/blog/2015/12/atlassian-docker-orchestration/
- http://technologyconversations.com/2015/12/08/blue-green-deployment-to-docker-swarm-with-jenkins-workflow-plugin/
- http://technologyconversations.com/2015/11/25/deploying-containers-with-docker-swarm-and-docker-networking/
- http://www.javacodegeeks.com/2015/11/deploying-containers-docker-swarm-docker-networking.html
 
 
