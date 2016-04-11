/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.collect.Lists;

public class DockerComposeDescriptor {

    private final File yamlFile;
    private List<DockerConfigurationItem> items;

    public DockerComposeDescriptor(final File yamlFile) {
        this.yamlFile = yamlFile;
        this.items = Lists.newArrayList();
        process();
    }

    private void process() {
        YamlReader reader = null;
        try {
            reader = new YamlReader(new FileReader(yamlFile));
            Map items = (Map) reader.read();
            //System.out.println(items);
            if (items.containsKey("version") && items.get("version").equals("2")) {
                if (items.containsKey("services")) {
                    final Map services = (Map) items.get("services");
                    for (Object o : services.keySet()) {
                        this.items.add(new DockerComposeImageItem(o.toString(), toMap(services.get(o))));
                    }
                }
                if (items.containsKey("volumes")) {
                    final Map volumes = (Map) items.get("volumes");
                    for (Object o : volumes.keySet()) {
                        this.items.add(new DockerComposeVolumeItem(o.toString(), toMap(volumes.get(o))));
                    }
                }
                if (items.containsKey("networks")) {
                    final Map networks = (Map) items.get("networks");
                    for (Object o : networks.keySet()) {
                        this.items.add(new DockerComposeNetworkItem(o.toString(), toMap(networks.get(o))));
                    }
                }
            } else {
                for (Object o : items.keySet()) {
                    this.items.add(new DockerComposeImageItem(o.toString(), (Map) items.get(o)));
                }
            }


        } catch (YamlException e) {
            throw new RuntimeException("Yaml parsing failed", e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot read the file " + yamlFile, e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map toMap(Object o) {
        if (o instanceof Map) {
            Map map = (Map) o;
            return map;
        }
        return Collections.emptyMap();
    }

    public List<DockerConfigurationItem> getItems() {
        return items;
    }

    public DockerConfigurationItem getItemByName(String name) {
        for (DockerConfigurationItem item : items) {
            if (item.getName().equals(name))
                return item;
        }
        throw new RuntimeException("Image not found ['" + name + "']");
    }

}
