package uk.ac.cam.cl.cadets;

import java.util.Map;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

class OpusSinkConfig extends AbstractConfig {

    public static final ConfigDef CONFIG_DEF = new ConfigDef();

    OpusSinkConfig(Map<? ,?> props) {
        super(CONFIG_DEF, props);
    }
}
