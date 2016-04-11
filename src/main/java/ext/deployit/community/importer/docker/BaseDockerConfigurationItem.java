/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;

import java.util.Map;
import java.util.regex.Pattern;

public abstract class BaseDockerConfigurationItem implements DockerConfigurationItem {

    final protected String type;
    final protected String name;
    final protected Map properties;

    public BaseDockerConfigurationItem(final String type, final String name, final Map properties) {
        this.type = type;
        this.name = name;
        this.properties = properties;
        checkIfItIsABuildImage();
    }

    private void checkIfItIsABuildImage() {
        //TODO check if the properies contains "build"
        //if yes throw a runtime Exception.
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    /*
    pattern 1 $XX => {{XX}}
    pattern 2 ${XX} => {{XX}}
    $$XX => $$XX
    */
    public static String translateToPropertyPlaceholder(String var) {
        if (var.contains("$$")) {
            return var;
        }

        Pattern pattern1 = Pattern.compile("\\$([a-zA-Z1-9\\._]+)");
        String var1 = pattern1.matcher(var).replaceAll("{{$1}}");

        Pattern pattern2 = Pattern.compile("\\$(\\{[^\\}]+\\})");
        final String var2 = pattern2.matcher(var1).replaceAll("{$1}");

        return var2;
    }

    protected String toCiName(String s) {
        return translateToPropertyPlaceholder(s.replace(":", "_"));
    }
}
