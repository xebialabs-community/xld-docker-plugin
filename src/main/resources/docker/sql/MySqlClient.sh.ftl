<#--

    THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
    FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.

-->
#!/bin/sh
<#import "/sql/commonFunctions.ftl" as cmn>
<#include "/generic/templates/linuxExportEnvVars.ftl">

<#if !cmn.lookup('username')??>
echo 'ERROR: username not specified! Specify it in either SqlScripts or its MySqlClient container'
exit 1
<#else>

<#assign cmdLine = ["docker", "run","-t","--rm"] />
<#assign cmdLine = cmdLine + ["-v `pwd`:/tmp/xebialabs"]/>
<#assign cmdLine = cmdLine + ["--link ${deployed.container.name}:mysql"]/>
<#assign cmdLine = cmdLine + ["${deployed.container.image}"]/>
<#assign cmdLine = cmdLine + ['sh','-c']/>
<#assign sqlcmdLine = ['mysql', '-h"$MYSQL_PORT_3306_TCP_ADDR"','-P"$MYSQL_PORT_3306_TCP_PORT"',"-u${cmn.lookup('username')}","-p${cmn.lookup('password')}", '</tmp/xebialabs/${step.artifact.name}' ] />

echo <#list cmdLine as item>${item} </#list>'<#list sqlcmdLine as item>${item} </#list>'
<#list cmdLine as item>${item} </#list>'<#list sqlcmdLine as item>${item} </#list>'

</#if>