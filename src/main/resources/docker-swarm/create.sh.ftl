<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">

echo "Running ${deployed.id}"
<#assign cmdLine = ["docker", "service", "create"] />

<#if (deployed.limitMemory??)>
    <#assign cmdLine = cmdLine + ["--limit-memory ${deployed.limitMemory}" ]/>
</#if>

<#if (deployed.reserveMemory??)>
    <#assign cmdLine = cmdLine + ["--reserve-memory ${deployed.reserveMemory}" ]/>
</#if>

<#if (deployed.replicas??)>
    <#assign cmdLine = cmdLine + ["--replicas ${deployed.replicas}" ]/>
</#if>
<#if (deployed.updateDelay??)>
    <#assign cmdLine = cmdLine + ["--update-delay ${deployed.updateDelay}" ]/>
</#if>
<#if (deployed.logDriver??)>
    <#assign cmdLine = cmdLine + ["--log-driver ${deployed.logDriver}" ]/>
</#if>
<#list deployed.ports as port>
    <#assign cmdLine = cmdLine + ["--publish ${port.hostPort}:${port.containerPort}"]/>
</#list>
<#list deployed.labels as label>
    <#assign cmdLine = cmdLine + ["--label ${label}"]/>
</#list>
<#list deployed.variables as variable>
    <#assign cmdLine = cmdLine + ["--env \"${variable.name}${variable.separator}${variable.value}\""]/>
</#list>
<#if (deployed.network??)>
    <#assign cmdLine = cmdLine + ["--network ${deployed.network}"]/>
</#if>
<#assign cmdLine = cmdLine + ["--name ${deployed.name}"]/>
<#assign cmdLine = cmdLine + ["${deployed.image}"]/>

<#if (deployed.command??)>
    <#assign cmdLine = cmdLine + ["${deployed.command}"]/>
</#if>
<#if (deployed.args??)>
    <#assign cmdLine = cmdLine + ["${deployed.args}"]/>
</#if>


echo <#list cmdLine as item>${item} </#list>
<#list cmdLine as item>${item} </#list>


