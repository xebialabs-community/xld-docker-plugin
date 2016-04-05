<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
<#include "/docker/setup-docker.ftl">
<#list deployed.placeholders?keys as k>
export ${k}=${deployed.placeholders[k]}
</#list>

docker-compose --file ${composed.file.path} --project-name ${application} up <#if (deployed.forceRecreate)>--force-recreate</#if> --no-color --no-build -d
