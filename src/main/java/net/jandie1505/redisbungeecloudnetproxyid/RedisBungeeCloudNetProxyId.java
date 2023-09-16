package net.jandie1505.redisbungeecloudnetproxyid;

import eu.cloudnetservice.common.log.LogManager;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.module.ModuleLifeCycle;
import eu.cloudnetservice.driver.module.ModuleTask;
import eu.cloudnetservice.driver.module.driver.DriverModule;
import eu.cloudnetservice.node.event.service.CloudServicePreProcessStartEvent;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class RedisBungeeCloudNetProxyId extends DriverModule {

    @ModuleTask(lifecycle = ModuleLifeCycle.STARTED)
    public void onStart(
            EventManager eventManager
    ) {
        eventManager.registerListener(this);
        LogManager.logger(RedisBungeeCloudNetProxyId.class).info("Started RedisBungeeCloudNetProxyId");
    }

    @ModuleTask(lifecycle = ModuleLifeCycle.STOPPED)
    public void onStop(
            EventManager eventManager
    ) {
        eventManager.unregisterListener(this);
        LogManager.logger(RedisBungeeCloudNetProxyId.class).info("Stopped RedisBungeeCloudNetProxyId");
    }

    @EventListener
    public void onCloudServicePreProcessStart(CloudServicePreProcessStartEvent event) {

        Path configPath = event.service().pluginDirectory().resolve("RedisBungee").resolve("config.yml");

        if (Files.exists(configPath) && Files.isRegularFile(configPath)) {
            String serviceName = event.service().serviceId().name();

            try {

                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(Files.newBufferedReader(configPath));

                data.put("proxy-id", serviceName);

                yaml.dump(data, Files.newBufferedWriter(configPath));

                LogManager.logger(RedisBungeeCloudNetProxyId.class).fine("Updated server context in RedisBungee configuration");

            } catch (IOException e) {
                LogManager.logger(RedisBungeeCloudNetProxyId.class).warning("Error while reading/writing RedisBungee configuration", e);
            }

        }

    }

}
