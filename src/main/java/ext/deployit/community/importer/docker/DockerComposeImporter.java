/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import com.google.common.collect.Lists;

import com.xebialabs.deployit.plugin.api.udm.Application;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Version;
import com.xebialabs.deployit.server.api.importer.*;

import de.schlichtherle.truezip.file.TFile;

import static com.google.common.collect.Lists.newArrayList;

public class DockerComposeImporter implements ListableImporter {

    private RepositoryService repositoryService;

    @Override
    public List<String> list(final File directory) {
        final String[] list = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".yaml") || name.endsWith(".yml");
            }
        });
        return newArrayList(list);
    }

    @Override
    public boolean canHandle(final ImportSource source) {
        final String absolutePath = source.getFile().getAbsolutePath();
        return absolutePath.endsWith(".yaml") || absolutePath.endsWith("yml");
    }

    @Override
    public PackageInfo preparePackage(final ImportSource source, final ImportingContext context) {
        final PackageInfo packageInfo = new PackageInfo(source);
        packageInfo.setApplicationName(source.getFile().getName().replace(".yaml", "").replace(".yml", ""));
        packageInfo.setApplicationVersion("V" + System.currentTimeMillis());
        context.setAttribute("temporaryFiles", Lists.<TFile>newArrayList());
        return packageInfo;
    }

    @Override
    public ImportedPackage importEntities(final PackageInfo packageInfo, final ImportingContext context) {
        Application application = getRepositoryService().newApplication("udm.Application", packageInfo.getApplicationId());
        Version version = getRepositoryService().newVersion("udm.DeploymentPackage", String.format("%s/%s", application.getId(), packageInfo.getApplicationVersion()));
        final ImportedPackage importedPackage = new ImportedPackage(packageInfo, application, version);
        final DockerComposeDescriptor descriptor = new DockerComposeDescriptor(packageInfo.getSource().getFile());
        for (DockerConfigurationItem item : descriptor.getItems()) {
            final String imageId = String.format("%s/%s", version.getId(), item.getName());
            Deployable deployable = (Deployable) item.toConfigurationItem(imageId, repositoryService);
            importedPackage.addDeployable(deployable);
        }
        return importedPackage;
    }

    @Override
    public void cleanUp(final PackageInfo packageInfo, final ImportingContext context) {

    }

    public RepositoryService getRepositoryService() {
        if (repositoryService == null) {
            repositoryService = new DefaultRepositoryService();
        }
        return repositoryService;
    }

    public void setRepositoryService(final RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

}
