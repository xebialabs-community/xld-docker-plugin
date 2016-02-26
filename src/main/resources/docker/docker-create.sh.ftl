<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">

echo "Running ${deployed_container.id}"
<#assign cmdLine = ["docker", "create"] />

<#if (deployed_container.publishAllExposedPorts)>
    <#assign cmdLine = cmdLine + ["-P"]/>
</#if>
<#if (deployed_container.restartAlways)>
    <#assign cmdLine = cmdLine + ["--restart=always"]/>
</#if>
<#if (deployed_container.memory??)>
    <#assign cmdLine = cmdLine + ["--memory ${deployed_container.memory}" ]/>
</#if>
<#if (deployed_container.entryPoint??)>
    <#assign cmdLine = cmdLine + ["--entrypoint ${deployed_container.entryPoint}" ]/>
</#if>
<#list deployed_container.ports as port>
    <#if (port.protocol??)>
        <#assign cmdLine = cmdLine + ["--publish ${port.hostPort}:${port.containerPort}/${port.protocol}"]/>
    <#else>
        <#assign cmdLine = cmdLine + ["--publish ${port.hostPort}:${port.containerPort}"]/>
    </#if>
</#list>
<#list deployed_container.links as link>
    <#assign cmdLine = cmdLine + ["--link=${link.name}:${link.alias}"]/>
</#list>
<#list deployed_container.volumes as volume>
    <#assign cmdLine = cmdLine + ["-v ${volume.name}:${volume.containerPath}"]/>
</#list>
<#list deployed_container.volumesFrom as volume>
    <#assign cmdLine = cmdLine + ["--volumes-from=${volume}"]/>
</#list>
<#list deployed_container.labels as label>
    <#assign cmdLine = cmdLine + ["--label=${label}"]/>
</#list>
<#list deployed_container.variables as variable>
    <#assign cmdLine = cmdLine + ["-e \"${variable.name}${variable.separator}${variable.value}\""]/>
</#list>
<#if (deployed_container.network??)>
    <#assign cmdLine = cmdLine + ["--net=${deployed_container.network}"]/>
</#if>
<#assign cmdLine = cmdLine + ["--name ${deployed_container.name}"]/>
<#assign cmdLine = cmdLine + ["${deployed_container.image}"]/>

<#if (deployed_container.command??)>
    <#assign cmdLine = cmdLine + ["${deployed_container.command}"]/>
</#if>
<#if (deployed_container.args??)>
    <#assign cmdLine = cmdLine + ["${deployed_container.args}"]/>
</#if>


echo <#list cmdLine as item>${item} </#list>
<#list cmdLine as item>${item} </#list>

