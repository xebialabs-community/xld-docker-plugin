<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
echo "Creating swarm docker '${deployed.machineName}' machine ..."
<#assign cmdLine = ["docker-machine", "create","--driver","${deployed.driver}","--swarm"] />
<#if deployed.master>
    <#assign cmdLine = cmdLine + ["--swarm-master"]/>
</#if>

<#list deployed.insecureRegistries as registry>
    <#assign cmdLine = cmdLine + ["--engine-insecure-registry",registry]/>
</#list>

<#list deployed.engineOptions as option>
    <#assign cmdLine = cmdLine + ["--engine-opt","\"${option}\""]/>
</#list>

<#list deployed.engineLabels as label>
    <#assign cmdLine = cmdLine + ["--engine-label","\"${label}\""]/>
</#list>

<#assign cmdLine = cmdLine + ["--swarm-discovery","${deployed.swarmDiscovery}"]/>
<#assign cmdLine = cmdLine + ["--swarm-image","${deployed.swarmImage}"]/>
<#assign cmdLine = cmdLine + ["--swarm-strategy","${deployed.swarmStrategy}"]/>
<#assign cmdLine = cmdLine + ["--swarm-host","${deployed.swarmHost}"]/>

<#assign cmdLine = cmdLine + ["${deployed.machineName}"]/>
echo Executing <#list cmdLine as item>${item} </#list>
<#list cmdLine as item>${item} </#list>

