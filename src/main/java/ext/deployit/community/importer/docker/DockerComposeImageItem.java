/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.google.common.base.Function;

import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Sets.newHashSet;

public class DockerComposeImageItem extends BaseDockerConfigurationItem {

    public DockerComposeImageItem(final String name, final Map properties) {
        super("docker.Image", name, properties);
    }

    public String getImage() {
        return (String) properties.get("image");
    }

    public String getName() {
        return name;
    }

    public List<String> getPorts() {
        return (List<String>) (properties.containsKey("ports") ? properties.get("ports") : Collections.emptyList());

    }

    public List<String> getLinks() {
        return (List<String>) (properties.containsKey("links") ? properties.get("links") : Collections.emptyList());
    }

    public Map<String, String> getEnvironments() {
        return (Map<String, String>) (properties.containsKey("environment") ? properties.get("environment") : Collections.emptyMap());
    }

    public List<String> getVolumes() {
        return (List<String>) (properties.containsKey("volumes") ? properties.get("volumes") : Collections.emptyList());

    }

    public List<String> getNetworks() {
        return (List<String>) (properties.containsKey("networks") ? properties.get("networks") : Collections.emptyList());
    }

    public String getType() {
        return type;
    }

    @Override
    public ConfigurationItem toConfigurationItem(final String id, final RepositoryService service) {
        ConfigurationItem ci = service.newCI(getType(), id);
        DockerComposeImageItem item = this;
        final String imageId = ci.getId();
        ci.setProperty("image", translateToPropertyPlaceholder(item.getImage()));
        ci.setProperty("ports", newHashSet(transform(item.getPorts(), new Function<String, ConfigurationItem>() {
            @Override
            public ConfigurationItem apply(final String s) {
                final String id = String.format("%s/%s", imageId, toCiName(s));
                ConfigurationItem ci = service.newCI("docker.PortSpec", id);
                ci.setProperty("hostPort", translateToPropertyPlaceholder(s.split(":")[0]));
                ci.setProperty("containerPort", translateToPropertyPlaceholder(s.split(":")[1]));
                return ci;
            }
        })));
        ci.setProperty("links", newHashSet(transform(item.getLinks(), new Function<String, ConfigurationItem>() {
            @Override
            public ConfigurationItem apply(final String s) {
                if (s.contains(":")) {
                    final String id = String.format("%s/%s", imageId, s.split(":")[0]);
                    ConfigurationItem ci = service.newCI("docker.LinkSpec", id);
                    ci.setProperty("alias", translateToPropertyPlaceholder(s.split(":")[1]));
                    return ci;
                } else {
                    final String id = String.format("%s/%s", imageId, toCiName(s));
                    ConfigurationItem ci = service.newCI("docker.LinkSpec", id);
                    ci.setProperty("alias", translateToPropertyPlaceholder(s));
                    return ci;
                }
            }
        })));
        ci.setProperty("variables", newHashSet(transform(newArrayList(item.getEnvironments().entrySet()), new Function<Map.Entry<String, String>, ConfigurationItem>() {
            @Override
            public ConfigurationItem apply(final Map.Entry<String, String> s) {
                final String id = String.format("%s/%s", imageId, toCiName(s.getKey()));
                ConfigurationItem ci = service.newCI("docker.EnvironmentVariableSpec", id);
                ci.setProperty("value", translateToPropertyPlaceholder(s.getValue()));
                return ci;
            }
        })));
        ci.setProperty("volumes", newHashSet(transform(item.getVolumes(), new Function<String, ConfigurationItem>() {
            @Override
            public ConfigurationItem apply(final String s) {
                if (s.contains(":")) {
                    final String[] split = s.split(":");
                    String name = translateToPropertyPlaceholder(split[0].replace('/', '_'));
                    final String id = String.format("%s/%s", imageId, name);
                    ConfigurationItem ci = service.newCI("docker.VolumeSpec", id);
                    ci.setProperty("source", translateToPropertyPlaceholder(split[0]));
                    ci.setProperty("destination", translateToPropertyPlaceholder(split[1]));
                    return ci;
                } else {
                    throw new RuntimeException("Cannot convert to VolumeSpec " + s);
                }
            }
        })));

        return ci;

    }
}
