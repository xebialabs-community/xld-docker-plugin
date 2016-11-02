<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#if (target.setEnvironmentVariables)>
<#if (target.dynamicParameters) >
    echo "call the docker-machine commandline: docker-machine env ${target.name}"
    eval "$(docker-machine env ${target.name})"
<#else>
export DOCKER_TLS_VERIFY="${target.tls_verify?string('1', '0')}"
export DOCKER_HOST="tcp://${target.address}:${target.port}"
export DOCKER_CERT_PATH="${target.certificatePath}"
export DOCKER_MACHINE_NAME=${target.name}
</#if>
<#else>
# Stop Managing the environment variables ;-)
</#if>
