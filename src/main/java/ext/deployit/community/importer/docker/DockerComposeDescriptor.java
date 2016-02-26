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
    private List<DockerComposeItem> images;

    public DockerComposeDescriptor(final File yamlFile) {
        this.yamlFile = yamlFile;
        this.images = Lists.newArrayList();
        process();
    }

    private void process() {
        YamlReader reader = null;
        try {
            reader = new YamlReader(new FileReader(yamlFile));
            Map items = (Map) reader.read();
            System.out.println(items);
            for (Object o : items.keySet()) {
                images.add(new DockerComposeItem(o.toString(), (Map) items.get(o)));
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

    public List<DockerComposeItem> getImages() {
        return images;
    }

    static class DockerComposeItem {
        final private String name;
        final private Map properties;

        DockerComposeItem(final String name, final Map properties) {
            this.name = name;
            this.properties = properties;
        }

        public String getImage() {
            return (String) properties.get("image");
        }

        public String getName() {
            return name;
        }

        public Map getProperties() {
            return properties;
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
    }
}
