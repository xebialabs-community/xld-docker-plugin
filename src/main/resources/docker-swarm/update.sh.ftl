<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">

echo "Running ${deployed.id}"
<#assign cmdLine = ["docker", "service", "update"] />

<#if (deployed.replicas??)>
    <#assign cmdLine = cmdLine + ["--replicas ${deployed.replicas}" ]/>
</#if>
<#if (deployed.updateDelay??)>
    <#assign cmdLine = cmdLine + ["--update-delay ${deployed.updateDelay}" ]/>
</#if>
<#if (deployed.logDriver??)>
    <#assign cmdLine = cmdLine + ["--log-driver ${deployed.logDriver}" ]/>
</#if>

<#list previousDeployed.ports as port>
    <#assign cmdLine = cmdLine + ["--publish-rm ${port.containerPort}"]/>
</#list>

<#list deployed.ports as port>
    <#assign cmdLine = cmdLine + ["--publish-add ${port.hostPort}:${port.containerPort}"]/>
</#list>

<#list previousDeployed.labels as label>
    <#assign cmdLine = cmdLine + ["--label-rm ${label}"]/>
</#list>
<#list deployed.labels as label>
    <#assign cmdLine = cmdLine + ["--label-add ${label}"]/>
</#list>

<#list previousDeployed.variables as variable>
    <#assign cmdLine = cmdLine + ["--env-rm \"${variable.name}${variable.separator}${variable.value}\""]/>
</#list>
<#list deployed.variables as variable>
    <#assign cmdLine = cmdLine + ["--env-add \"${variable.name}${variable.separator}${variable.value}\""]/>
</#list>

<#if (previousDeployed.network??)>
    <#assign cmdLine = cmdLine + ["--network-rm ${previousDeployed.network}"]/>
</#if>
<#if (deployed.network??)>
    <#assign cmdLine = cmdLine + ["--network-add ${deployed.network}"]/>
</#if>

<#if (deployed.image??)>
<#assign cmdLine = cmdLine + ["--image ${deployed.image}"]/>
</#if>
<#assign cmdLine = cmdLine + ["${deployed.name}"]/>


echo <#list cmdLine as item>${item} </#list>
<#list cmdLine as item>${item} </#list>


