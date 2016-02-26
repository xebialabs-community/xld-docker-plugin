/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.importer.docker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Map;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Application;
import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.Version;
import com.xebialabs.deployit.server.api.importer.*;

import de.schlichtherle.truezip.file.TFile;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Sets.newHashSet;

public class DockerComposeImporter implements ListableImporter {
    @Override
    public List<String> list(final File directory) {
        final String[] list = directory.list(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith(".yaml");
            }
        });
        return newArrayList(list);
    }

    @Override
    public boolean canHandle(final ImportSource source) {
        return source.getFile().getAbsolutePath().endsWith(".yaml");
    }

    @Override
    public PackageInfo preparePackage(final ImportSource source, final ImportingContext context) {
        final PackageInfo packageInfo = new PackageInfo(source);
        packageInfo.setApplicationName(source.getFile().getName().replace(".yaml", ""));
        packageInfo.setApplicationVersion("V" + System.currentTimeMillis());
        context.setAttribute("temporaryFiles", Lists.<TFile>newArrayList());
        return packageInfo;
    }

    @Override
    public ImportedPackage importEntities(final PackageInfo packageInfo, final ImportingContext context) {

        Application application = Type.valueOf(Application.class).getDescriptor().newInstance(packageInfo.getApplicationId());
        Version version = Type.valueOf("udm.DeploymentPackage").getDescriptor().newInstance(String.format("%s/%s", application.getId(), packageInfo.getApplicationVersion()));

        final ImportedPackage importedPackage = new ImportedPackage(packageInfo, application, version);
        final DockerComposeDescriptor descriptor = new DockerComposeDescriptor(packageInfo.getSource().getFile());
        for (DockerComposeDescriptor.DockerComposeItem item : descriptor.getImages()) {
            final String imageId = String.format("%s/%s", version.getId(), item.getName());
            Deployable deployable = Type.valueOf("docker.Image").getDescriptor().newInstance(imageId);
            deployable.setProperty("image", item.getImage());
            deployable.setProperty("ports", newHashSet(transform(item.getPorts(), new Function<String, ConfigurationItem>() {
                @Override
                public ConfigurationItem apply(final String s) {
                    final String id = String.format("%s/%s", imageId, toCiName(s));
                    ConfigurationItem ci = Type.valueOf("docker.PortSpec").getDescriptor().newInstance(id);
                    ci.setProperty("hostPort", s.split(":")[0]);
                    ci.setProperty("containerPort", s.split(":")[1]);
                    return ci;
                }
            })));
            deployable.setProperty("links", newHashSet(transform(item.getLinks(), new Function<String, ConfigurationItem>() {
                @Override
                public ConfigurationItem apply(final String s) {
                    if (s.contains(":")) {
                        final String id = String.format("%s/%s", imageId, s.split(":")[0]);
                        ConfigurationItem ci = Type.valueOf("docker.LinkSpec").getDescriptor().newInstance(id);
                        ci.setProperty("alias", s.split(":")[1]);
                        return ci;
                    } else {
                        final String id = String.format("%s/%s", imageId, toCiName(s));
                        ConfigurationItem ci = Type.valueOf("docker.LinkSpec").getDescriptor().newInstance(id);
                        ci.setProperty("alias", s);
                        return ci;
                    }
                }
            })));
            deployable.setProperty("variables", newHashSet(transform(newArrayList(item.getEnvironments().entrySet()), new Function<Map.Entry<String, String>, ConfigurationItem>() {
                @Override
                public ConfigurationItem apply(final Map.Entry<String, String> s) {
                    final String id = String.format("%s/%s", imageId, toCiName(s.getKey()));
                    ConfigurationItem ci = Type.valueOf("docker.EnvironmentVariableSpec").getDescriptor().newInstance(id);
                    ci.setProperty("value", s.getValue());
                    return ci;
                }
            })));
            importedPackage.addDeployable(deployable);
        }
        return importedPackage;
    }

    @Override
    public void cleanUp(final PackageInfo packageInfo, final ImportingContext context) {

    }

    private String toCiName(String s) {
        return s.replace(":", "_");
    }
}
