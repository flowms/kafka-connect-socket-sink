package uk.ac.cam.cl.cadets;

import java.util.Collection;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpusSinkTask extends SinkTask {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(OpusSinkTask.class);

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> offsets) {
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            return;
        }
    }

    @Override
    public void start(Map<String, String> args) {
    }

    @Override
    public void stop() {
    }

    @Override
    public String version() {
        return Version.getVersion();
    }
}
