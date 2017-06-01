# Kafka Connect Unix Socket Sink
The connector is used to write data from Kafka to a Unix Socket.

# Building
You can build the connector with Maven using the standard lifecycle phases:
```
mvn clean
mvn package
```

# Sample Configuration
``` ini
name=socket-connector
connector.class=uk.cl.cam.ac.uk.cadets.UnixSocketSinkConnector
tasks.max=1
topics=topic_name
schema.name=socketschema
pathname=socket_name
```
