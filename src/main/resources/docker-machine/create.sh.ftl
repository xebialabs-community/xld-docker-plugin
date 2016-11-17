<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
echo "Creating docker '${deployed.name}' machine using ${deployed.driver} provider"
<#assign cmdLine = ["docker-machine", "create","--driver","${deployed.driver}"] />

<#list deployed.insecureRegistries as registry>
    <#assign cmdLine = cmdLine + ["--engine-insecure-registry",registry]/>
</#list>

<#list deployed.engineOptions as option>
    <#assign cmdLine = cmdLine + ["--engine-opt","\"${option}\""]/>
</#list>

<#list deployed.engineLabels as label>
    <#assign cmdLine = cmdLine + ["--engine-label","\"${label}\""]/>
</#list>

<#if (deployed.machineName??)>
    <#assign cmdLine = cmdLine + ["${deployed.machineName}"]/>
<#else>
    <#assign cmdLine = cmdLine + ["${deployed.name}"]/>
</#if>
echo Executing <#list cmdLine as item>${item} </#list>
<#list cmdLine as item>${item} </#list>
