/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DockerComposeDescriptorTest {

    @org.junit.Test
    public void testProcess() throws Exception {

        final File yamlFile = new File("src/test/resources/docker-compose-test.yaml");
        assertTrue(yamlFile.exists());
        DockerComposeDescriptor descriptor = new DockerComposeDescriptor(yamlFile);
        final List<DockerComposeDescriptor.DockerComposeItem> images = descriptor.getImages();
        assertEquals(3, images.size());

        for (DockerComposeDescriptor.DockerComposeItem image : images) {
            if (image.getName().equals("haproxy")) {
                assertEquals("eeacms/haproxy", image.getImage());
                assertEquals(2, image.getPorts().size());
                assertEquals("80:80", image.getPorts().get(0));
                assertEquals("1936:1936", image.getPorts().get(1));
                assertEquals(1, image.getLinks().size());
                assertEquals("webapp", image.getLinks().get(0));
            }
            if (image.getName().equals("webapp")) {
                assertEquals("razvan3895/nodeserver", image.getImage());
                assertTrue(image.getPorts().isEmpty());
                assertTrue(image.getLinks().isEmpty());
                assertTrue(image.getEnvironments().isEmpty());
            }

            if (image.getName().equals("mysqldb")) {
                assertEquals("mysql:latest", image.getImage());
                assertEquals(4, image.getEnvironments().size());
                assertEquals("supersecret", image.getEnvironments().get("MYSQL_ROOT_PASSWORD"));
                assertEquals("mysql", image.getEnvironments().get("MYSQL_PASSWORD"));
                assertEquals("sample", image.getEnvironments().get("MYSQL_DATABASE"));
                assertEquals("mysql", image.getEnvironments().get("MYSQL_USER"));
            }

        }
    }
}