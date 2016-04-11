/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;

import java.util.Map;

import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;

public class DockerComposeVolumeItem extends BaseDockerConfigurationItem {

    public DockerComposeVolumeItem(final String name, final Map properties) {
        super("docker.Folder", name, properties);
    }

    @Override
    public ConfigurationItem toConfigurationItem(String id, RepositoryService service) {
        ConfigurationItem ci = service.newCI(getType(), id);
        return ci;
    }
}
