<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">
echo "Check if ${deployed.name} is running ....."
echo "docker inspect -f {{.State.Running}} ${deployed.name}"
docker inspect -f {{.State.Running}} ${deployed.name}
docker inspect -f {{.State.Running}} ${deployed.name}  | grep 'true'
