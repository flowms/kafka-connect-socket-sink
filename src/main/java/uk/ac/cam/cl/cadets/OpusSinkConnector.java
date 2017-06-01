package uk.ac.cam.cl.cadets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.connector.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpusSinkConnector extends SinkConnector {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(OpusSinkConnector.class);

    private Map<String, String> configProps;

    @Override
    public ConfigDef config() {
        return OpusSinkConfig.CONFIG_DEF;
    }

    @Override
    public void start(Map<String, String> props) {
        configProps = props;
    }

    @Override
    public void stop() {
    }

    @Override
    public Class<? extends Task> taskClass() {
        return OpusSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        LOGGER.info("Setting task configurations for {} workers", maxTasks);
        final List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; i++) {
            configs.add(configProps);
        }
        return configs;
    }

    //@Override
    //public Config validate(Map<String, String> connectorConfigs) {
    //    return super.validate(connectorConfigs);
    //}

    @Override
    public String version() {
        return Version.getVersion();
    }
}
