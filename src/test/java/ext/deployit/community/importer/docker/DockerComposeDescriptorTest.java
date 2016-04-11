/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;

import java.io.File;
import java.util.List;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class DockerComposeDescriptorTest {

    @org.junit.Test
    public void testProcessTest1() throws Exception {

        final File yamlFile = new File("src/test/resources/docker-compose-test.yaml");
        assertTrue(yamlFile.exists());
        DockerComposeDescriptor descriptor = new DockerComposeDescriptor(yamlFile);
        final List<DockerConfigurationItem> images = descriptor.getItems();
        assertEquals(3, images.size());

        for (DockerConfigurationItem i : images) {
            DockerComposeImageItem image = (DockerComposeImageItem) i;
            if (image.getName().equals("haproxy")) {
                assertEquals("eeacms/haproxy", image.getImage());
                assertEquals("docker.Image", image.getType());
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

    @org.junit.Test
    public void testProcessTest2() throws Exception {

        final File yamlFile = new File("src/test/resources/docker-compose-test-2.yaml");
        assertTrue(yamlFile.exists());
        DockerComposeDescriptor descriptor = new DockerComposeDescriptor(yamlFile);
        final List<DockerConfigurationItem> images = descriptor.getItems();
        assertEquals(1, images.size());

        final DockerComposeImageItem image = (DockerComposeImageItem) images.iterator().next();
        assertEquals("tauffredou/swarm-demo", image.getImage());
        assertEquals(2, image.getEnvironments().size());
        assertEquals("tcp://192.168.99.103:3376", image.getEnvironments().get("DOCKER_HOST"));
        assertEquals("/certs", image.getEnvironments().get("DOCKER_CERT_PATH"));

        assertEquals(1, image.getPorts().size());
        assertEquals("8080:8080", image.getPorts().get(0));

        assertEquals(2, image.getVolumes().size());
        assertEquals("/var/run/docker.sock:/var/run/docker.sock", image.getVolumes().get(0));
        assertEquals("/Users/thomas/.docker/machine/machines/mhs-demo0:/certs/", image.getVolumes().get(1));
    }


    @org.junit.Test
    public void testProcessTest3() throws Exception {

        final File yamlFile = new File("src/test/resources/docker-compose-v2.yaml");
        assertTrue(yamlFile.exists());
        DockerComposeDescriptor descriptor = new DockerComposeDescriptor(yamlFile);
        final List<DockerConfigurationItem> images = descriptor.getItems();
        assertEquals(8, images.size());

        final DockerComposeImageItem votingAppImage = (DockerComposeImageItem) descriptor.getItemByName("voting-app");
        assertEquals("docker.Image", votingAppImage.getType());
        assertEquals("voting-app:4", votingAppImage.getImage());
        assertEquals(1, votingAppImage.getPorts().size());
        assertEquals("5000:80", votingAppImage.getPorts().get(0));
        assertEquals(1, votingAppImage.getLinks().size());
        assertEquals("redis", votingAppImage.getLinks().get(0));
        assertEquals(2, votingAppImage.getNetworks().size());
        assertEquals("front-tier", votingAppImage.getNetworks().get(0));
        assertEquals("back-tier", votingAppImage.getNetworks().get(1));

        final DockerComposeImageItem workerImage = (DockerComposeImageItem) descriptor.getItemByName("worker");
        assertEquals("worker:56", workerImage.getImage());
        assertEquals(2, workerImage.getLinks().size());
        assertEquals("db", workerImage.getLinks().get(0));
        assertEquals("redis", workerImage.getLinks().get(1));

        final DockerComposeImageItem dbImage = (DockerComposeImageItem) descriptor.getItemByName("db");
        assertEquals("postgres:9.4", dbImage.getImage());
        assertEquals(1, dbImage.getVolumes().size());
        assertEquals("db-data:/var/lib/postgresql/data", dbImage.getVolumes().get(0));
        assertEquals(1, dbImage.getNetworks().size());
        assertEquals("back-tier", dbImage.getNetworks().get(0));

        final DockerComposeVolumeItem dbdataVolume = (DockerComposeVolumeItem) descriptor.getItemByName("db-data");
        assertEquals("docker.Folder", dbdataVolume.getType());

        final DockerComposeNetworkItem frontTierNetwork = (DockerComposeNetworkItem) descriptor.getItemByName("front-tier");
        assertEquals("docker.NetworkSpec", frontTierNetwork.getType());
        final DockerComposeNetworkItem backTierNetwork = (DockerComposeNetworkItem) descriptor.getItemByName("back-tier");
        assertEquals("docker.NetworkSpec", backTierNetwork.getType());

    }

    @Test
    public void testTranslateToPropertyPlaceholder() throws Exception {
        assertThat("ABC", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("ABC")));
        assertThat("{{ABC}}", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("$ABC")));
        assertThat("{{ABC}}", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("${ABC}")));
        assertThat("{{ABC_HOST}}", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("$ABC_HOST")));
        assertThat("{{ABC_HOST.A}}", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("$ABC_HOST.A")));

        assertThat("{{ABC_HOST}}", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("${ABC_HOST}")));
        assertThat("{{ABC_HOST_A}}", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("${ABC_HOST_A}")));

        assertThat("{{P1}}:{{P2}}", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("$P1:$P2")));
        assertThat("{{P1}}:{{P2}}", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("${P1}:${P2}")));
        assertThat("{{P1}}:{{P2}}", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("${P1}:$P2")));

        assertThat("$$ABC", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("$$ABC")));

        assertThat("ABC{{P1}}EDF:GHI{{P2}}JKL", new IsEqual(BaseDockerConfigurationItem.translateToPropertyPlaceholder("ABC${P1}EDF:GHI${P2}JKL")));
    }


}