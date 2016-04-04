package ext.deployit.community.importer.docker;

import java.io.File;
import org.junit.Test;

import com.xebialabs.deployit.server.api.importer.ImportSource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by bmoussaud on 04/04/16.
 */
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
}