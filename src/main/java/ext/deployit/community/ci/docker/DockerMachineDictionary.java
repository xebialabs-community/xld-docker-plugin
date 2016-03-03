/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 */
package ext.deployit.community.ci.docker;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.cache.*;
import com.google.common.collect.Maps;

import com.xebialabs.deployit.plugin.api.udm.Dictionary;
import com.xebialabs.deployit.plugin.api.udm.Metadata;
import com.xebialabs.deployit.plugin.api.udm.Property;
import com.xebialabs.deployit.plugin.overthere.Host;
import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.util.CapturingOverthereExecutionOutputHandler;

@Metadata(
        root = Metadata.ConfigurationItemRoot.ENVIRONMENTS,
        description = "A Dictionary that resolves the docker machine name -> ip"
)
public class DockerMachineDictionary extends Dictionary {

    @Property(description = "host on which the docker-* commands will be executed")
    private Host targetHost;

    @Property(description = "prefix used to build the key", defaultValue = "MACHINE-", category = "Advanced")
    private String keyPrefix;

    @Property(description = "Reload the entries during the planning phase", defaultValue = "True", category = "Advanced")
    private boolean dynamicLoad;

    @Property(description = "Use a cache (10 seconds)...", defaultValue = "True", category = "Advanced", hidden = true)
    private boolean useCache;


    @Property(description = "docker machine command used to fetch the name and ip", defaultValue = "docker-machine ls --format {{.Name}}={{.URL}}", category = "Advanced", hidden = true)
    private String dockerMachineLsCommand;

    public Map<String, String> getDockerMachines() {
        logger.debug("dockerMachineLsCommand = " + dockerMachineLsCommand);
        CmdLine cmdLine = new CmdLine();
        cmdLine.addTemplatedFragment(dockerMachineLsCommand);
        final OverthereConnection remoteConnection = targetHost.getConnection();
        final CapturingOverthereExecutionOutputHandler out = CapturingOverthereExecutionOutputHandler.capturingHandler();
        final CapturingOverthereExecutionOutputHandler err = CapturingOverthereExecutionOutputHandler.capturingHandler();
        int returnCode = remoteConnection.execute(out, err, cmdLine);
        if (returnCode == 0) {
            Map<String, String> machines = Maps.newHashMap();
            for (String line : out.getOutputLines()) {
                final String[] split = line.split("=");
                String key = keyPrefix + split[0];
                String value = "";
                if (split.length > 1) {
                    // tcp://192.168.99.100:2376
                    value = split[1].substring("tcp://".length()).split(":")[0];
                }
                machines.put(key, value);
            }
            return machines;
        } else {
            logger.error("Fail to get the docker machines :" + err.getOutput());
            return Collections.emptyMap();
        }
    }

    @Override
    public Map<String, String> getEntries() {
        if (dynamicLoad) {
            logger.debug("dynamicLoad True");
            Map data;
            if (useCache) {
                data = dockerMachinesCache.getUnchecked(this);
            } else {
                data = getDockerMachines();
            }
            logger.debug("docker-machines " + data);
            return data;
        } else {
            return super.getEntries();
        }
    }


    private static LoadingCache<DockerMachineDictionary, Map<String, String>> dockerMachinesCache =
            CacheBuilder.newBuilder()
                    .expireAfterAccess(10, TimeUnit.SECONDS)
                    .removalListener(new RemovalListener<Object, Object>() {
                        @Override
                        public void onRemoval(final RemovalNotification<Object, Object> removalNotification) {
                            logger.info("Remove " + removalNotification.getKey() + " from docker-machine cache.....");
                        }
                    })
                    .build(new CacheLoader<DockerMachineDictionary, Map<String, String>>() {
                        @Override
                        public Map<String, String> load(final DockerMachineDictionary s) throws Exception {
                            return s.getDockerMachines();
                        }
                    });

    private static Logger logger = LoggerFactory.getLogger(DockerMachineDictionary.class);
}
