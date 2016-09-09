<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">

echo "Delete service ${previousDeployed.name}"
<#assign cmdLine = ["docker", "service", "rm"] />
<#assign cmdLine = cmdLine + ["${previousDeployed.name}"]/>

echo <#list cmdLine as item>${item} </#list>
<#list cmdLine as item>${item} </#list>


