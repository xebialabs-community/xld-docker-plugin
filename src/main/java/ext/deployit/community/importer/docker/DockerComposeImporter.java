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
import java.util.regex.Pattern;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

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
        for (DockerComposeDescriptor.DockerComposeItem item : descriptor.getImages()) {
            final String imageId = String.format("%s/%s", version.getId(), item.getName());
            Deployable deployable = (Deployable) newCI("docker.Image", imageId);
            deployable.setProperty("image", translateToPropertyPlaceholder(item.getImage()));
            deployable.setProperty("ports", newHashSet(transform(item.getPorts(), new Function<String, ConfigurationItem>() {
                @Override
                public ConfigurationItem apply(final String s) {
                    final String id = String.format("%s/%s", imageId, toCiName(s));
                    ConfigurationItem ci = newCI("docker.PortSpec", id);
                    ci.setProperty("hostPort", translateToPropertyPlaceholder(s.split(":")[0]));
                    ci.setProperty("containerPort", translateToPropertyPlaceholder(s.split(":")[1]));
                    return ci;
                }
            })));
            deployable.setProperty("links", newHashSet(transform(item.getLinks(), new Function<String, ConfigurationItem>() {
                @Override
                public ConfigurationItem apply(final String s) {
                    if (s.contains(":")) {
                        final String id = String.format("%s/%s", imageId, s.split(":")[0]);
                        ConfigurationItem ci = newCI("docker.LinkSpec", id);
                        ci.setProperty("alias", translateToPropertyPlaceholder(s.split(":")[1]));
                        return ci;
                    } else {
                        final String id = String.format("%s/%s", imageId, toCiName(s));
                        ConfigurationItem ci = newCI("docker.LinkSpec", id);
                        ci.setProperty("alias", translateToPropertyPlaceholder(s));
                        return ci;
                    }
                }
            })));
            deployable.setProperty("variables", newHashSet(transform(newArrayList(item.getEnvironments().entrySet()), new Function<Map.Entry<String, String>, ConfigurationItem>() {
                @Override
                public ConfigurationItem apply(final Map.Entry<String, String> s) {
                    final String id = String.format("%s/%s", imageId, toCiName(s.getKey()));
                    ConfigurationItem ci = newCI("docker.EnvironmentVariableSpec", id);
                    ci.setProperty("value", translateToPropertyPlaceholder(s.getValue()));
                    return ci;
                }
            })));

            deployable.setProperty("volumes", newHashSet(transform(item.getVolumes(), new Function<String, ConfigurationItem>() {
                @Override
                public ConfigurationItem apply(final String s) {
                    if (s.contains(":")) {
                        final String[] split = s.split(":");
                        String name = translateToPropertyPlaceholder(split[0].replace('/', '_'));
                        final String id = String.format("%s/%s", imageId, name);
                        ConfigurationItem ci = newCI("docker.VolumeSpec", id);
                        ci.setProperty("source", translateToPropertyPlaceholder(split[0]));
                        ci.setProperty("destination", translateToPropertyPlaceholder(split[1]));
                        return ci;
                    } else {
                        throw new RuntimeException("Cannot convert to VolumeSpec " + s);
                    }
                }
            })));


            importedPackage.addDeployable(deployable);
        }
        return importedPackage;
    }

    private ConfigurationItem newCI(final String type, final String id) {
        return getRepositoryService().newCI(type, id);
    }

    /*
    pattern 1 $XX => {{XX}}
    pattern 2 ${XX} => {{XX}}
    $$XX => $$XX
     */
    static String translateToPropertyPlaceholder(String var) {
        if (var.contains("$$")) {
            return var;
        }

        Pattern pattern1 = Pattern.compile("\\$([a-zA-Z1-9\\._]+)");
        String var1 = pattern1.matcher(var).replaceAll("{{$1}}");

        Pattern pattern2 = Pattern.compile("\\$(\\{[^\\}]+\\})");
        final String var2 = pattern2.matcher(var1).replaceAll("{$1}");

        return var2;
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

    private String toCiName(String s) {
        return translateToPropertyPlaceholder(s.replace(":", "_"));
    }
}
