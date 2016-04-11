/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;


import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;

public interface DockerConfigurationItem {
    ConfigurationItem toConfigurationItem(String id,RepositoryService service);

    String getType();

    String getName();
}
