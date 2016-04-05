/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;

import java.io.File;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

import com.xebialabs.deployit.server.api.importer.ImportSource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class DockerComposeImporterTest {

    private DockerComposeImporter importer = new DockerComposeImporter();

    @Test
    public void testCanHandleYml() throws Exception {

        ImportSource source = new ImportSource() {

            @Override
            public File getFile() {
                return new File("accept.yml");
            }

            @Override
            public void cleanUp() {

            }
        };

        assertTrue(importer.canHandle(source));
    }

    @Test
    public void testCanHandleYaml() throws Exception {

        ImportSource source = new ImportSource() {

            @Override
            public File getFile() {
                return new File("accept.yaml");
            }

            @Override
            public void cleanUp() {

            }
        };

        assertTrue(importer.canHandle(source));
    }

    @Test
    public void testCanHandleXaml() throws Exception {

        ImportSource source = new ImportSource() {

            @Override
            public File getFile() {
                return new File("accept.xaml");
            }

            @Override
            public void cleanUp() {

            }
        };

        assertFalse(importer.canHandle(source));
    }


    @Test
    public void testTranslateToPropertyPlaceholder() throws Exception {
        assertThat("ABC", new IsEqual(importer.translateToPropertyPlaceholder("ABC")));
        assertThat("{{ABC}}", new IsEqual(importer.translateToPropertyPlaceholder("$ABC")));
        assertThat("{{ABC}}", new IsEqual(importer.translateToPropertyPlaceholder("${ABC}")));
        assertThat("{{ABC_HOST}}", new IsEqual(importer.translateToPropertyPlaceholder("$ABC_HOST")));
        assertThat("{{ABC_HOST.A}}", new IsEqual(importer.translateToPropertyPlaceholder("$ABC_HOST.A")));

        assertThat("{{ABC_HOST}}", new IsEqual(importer.translateToPropertyPlaceholder("${ABC_HOST}")));
        assertThat("{{ABC_HOST_A}}", new IsEqual(importer.translateToPropertyPlaceholder("${ABC_HOST_A}")));

        assertThat("{{P1}}:{{P2}}", new IsEqual(importer.translateToPropertyPlaceholder("$P1:$P2")));
        assertThat("{{P1}}:{{P2}}", new IsEqual(importer.translateToPropertyPlaceholder("${P1}:${P2}")));
        assertThat("{{P1}}:{{P2}}", new IsEqual(importer.translateToPropertyPlaceholder("${P1}:$P2")));

        assertThat("$$ABC", new IsEqual(importer.translateToPropertyPlaceholder("$$ABC")));

        assertThat("ABC{{P1}}EDF:GHI{{P2}}JKL", new IsEqual(importer.translateToPropertyPlaceholder("ABC${P1}EDF:GHI${P2}JKL")));
    }
}