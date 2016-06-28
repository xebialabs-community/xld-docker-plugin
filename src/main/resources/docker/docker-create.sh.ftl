<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">

echo "Running ${docker_container.id}"
<#assign cmdLine = ["docker", "create"] />

<#if (docker_container.publishAllExposedPorts)>
    <#assign cmdLine = cmdLine + ["--publish-all"]/>
</#if>
<#if (docker_container.restartAlways)>
    <#assign cmdLine = cmdLine + ["--restart=always"]/>
</#if>
<#if (docker_container.memory??)>
    <#assign cmdLine = cmdLine + ["--memory ${docker_container.memory}" ]/>
</#if>
<#if (docker_container.pidNamespace??)>
    <#assign cmdLine = cmdLine + ["--pid=${docker_container.pidNamespace}" ]/>
</#if>
<#if (docker_container.entryPoint??)>
    <#assign cmdLine = cmdLine + ["--entrypoint ${docker_container.entryPoint}" ]/>
</#if>
<#list docker_container.ports as port>
    <#if (port.protocol??)>
        <#assign cmdLine = cmdLine + ["--publish ${port.hostPort}:${port.containerPort}/${port.protocol}"]/>
    <#else>
        <#assign cmdLine = cmdLine + ["--publish ${port.hostPort}:${port.containerPort}"]/>
    </#if>
</#list>
<#list docker_container.links as link>
    <#assign cmdLine = cmdLine + ["--link=${container_prefix}${link.name}:${link.alias}"]/>
</#list>
<#list docker_container.volumes as volume>
    <#assign cmdLine = cmdLine + ["--volume=${volume.source}:${volume.destination}:${volume.mode}"]/>
</#list>
<#list docker_container.volumesFrom as volume>
    <#assign cmdLine = cmdLine + ["--volumes-from=${volume}"]/>
</#list>
<#list docker_container.labels as label>
    <#assign cmdLine = cmdLine + ["--label=${label}"]/>
</#list>
<#list docker_container.variables as variable>
    <#assign cmdLine = cmdLine + ["-e \"${variable.name}${variable.separator}${variable.value}\""]/>
</#list>
<#if (docker_container.network??)>
    <#assign cmdLine = cmdLine + ["--net=${docker_container.network}"]/>
</#if>
<#assign cmdLine = cmdLine + ["--name ${container_name}"]/>
<#assign cmdLine = cmdLine + ["${docker_container.image}"]/>

<#if (docker_container.command??)>
    <#assign cmdLine = cmdLine + ["${docker_container.command}"]/>
</#if>
<#if (docker_container.args??)>
    <#assign cmdLine = cmdLine + ["${docker_container.args}"]/>
</#if>


echo <#list cmdLine as item>${item} </#list>
<#list cmdLine as item>${item} </#list>

