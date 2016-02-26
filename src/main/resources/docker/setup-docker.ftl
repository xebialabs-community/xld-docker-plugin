<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->

<#if (target.dynamicParameters) >
    <#if (target.swarmMaster)>
    eval "$(docker-machine env ${target.name} --swarm)"
    <#else>
    eval "$(docker-machine env ${target.name})"
    </#if>
<#else>
    <#if (target.swarmMaster)>
export DOCKER_TLS_VERIFY="${target.tls_verify?string('1', '0')}"
export DOCKER_HOST="tcp://${target.address}:${target.swarmPort}"
export DOCKER_CERT_PATH="${target.certificatePath}"
export DOCKER_MACHINE_NAME=${target.name}
    <#else>
export DOCKER_TLS_VERIFY="${target.tls_verify?string('1', '0')}"
export DOCKER_HOST="tcp://${target.address}:${target.port}"
export DOCKER_CERT_PATH="${target.certificatePath}"
export DOCKER_MACHINE_NAME=${target.name}
    </#if>
</#if>
