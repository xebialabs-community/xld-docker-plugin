/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;


import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Application;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Version;

public class DefaultRepositoryService implements RepositoryService {
    @Override
    public ConfigurationItem newCI(final String type, final String id) {
        return Type.valueOf(type).getDescriptor().newInstance(id);
    }

    @Override
    public Application newApplication(final String type, final String id) {
        return (Application) newCI(type,id);
    }

    @Override
    public Version newVersion(final String type, final String id) {
        return (Version) newCI(type,id);
    }
}
