<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">
<#assign protocol = "tcp"/>
<#if (port.protocol??)>
    <#assign protocol="${port.protocol}"/>
</#if>
echo "Test the port on ${name}"
echo "docker port ${name} ${port.containerPort}/${protocol}"
docker port ${name} ${port.containerPort}/${protocol}
