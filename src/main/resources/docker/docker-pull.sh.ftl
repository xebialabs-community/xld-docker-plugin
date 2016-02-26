<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">

<#assign registry=""/>
<#if (deployed.registryHost??)>
    <#assign registry="${deployed.registryHost}:${deployed.registryPort}/"/>
</#if>

if docker inspect ${deployed.image} > /dev/null ; then
echo "${deployed.image} exists on the machine"
else
echo docker pull ${registry}${deployed.image}
docker pull ${registry}${deployed.image}
fi




