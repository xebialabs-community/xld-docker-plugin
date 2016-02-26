<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">
<#assign cmdLine = ["docker", "run","-t","--rm"] />
<#assign cmdLine = cmdLine + ["-v `pwd`:/tmp/xebialabs"]/>
<#list deployed.links as link>
    <#assign cmdLine = cmdLine + ["--link=${link}"]/>
</#list>
<#if (deployed.entryPoint??)>
<#assign cmdLine = cmdLine + ["--entrypoint '${deployed.entryPoint}'"]/>
</#if>
<#assign cmdLine = cmdLine + ["${deployed.image}"]/>
<#assign cmdLine = cmdLine + ["-c"]/>
<#assign cmdLine = cmdLine + ['"bash /tmp/xebialabs/${script}.sh"']/>

echo <#list cmdLine as item>${item} </#list>
<#list cmdLine as item>${item} </#list>


