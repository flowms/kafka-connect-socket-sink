/*-
 * Copyright (c) 2017 (Graeme Jenkinson)
 * All rights reserved.
 *
 * This software was developed by BAE Systems, the University of Cambridge
 * Computer Laboratory, and Memorial University under DARPA/AFRL contract
 * FA8650-15-C-7558 ("CADETS"), as part of the DARPA Transparent Computing
 * (TC) research program.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package uk.ac.cam.cl.cadets.kafka.connect.socketsink;

import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Type;

/***
 * Specifies configuration options for the SocketSinkConnector.
 */
final class SocketSinkConfig extends AbstractConfig {

    public static final ConfigDef CONFIG_DEF = new ConfigDef();

    /** Kafka topics to receive messages from */
    static final String KAFKA_TOPICS = "topics";
    static final String KAFKA_TOPICS_DELIMITER = ",";

    /** Copfiguration item to specify the hostname of the socket */
    static final String HOSTNAME = "hostname";
    static final String HOSTNAME_DOC = "Socket hostname";
    static final String HOSTNAME_DEFAULT = "localhost";

    /** Configuration item to specify the port of the socket */
    static final String PORT = "port";
    static final String PORT_DOC = "Socket port";
    static final int PORT_DEFAULT = 5432;

    public static final String MAX_RETRIES = "max.retries";
    private static final int MAX_RETRIES_DEFAULT = 10;
    private static final String MAX_RETRIES_DOC =
      "The maximum number of times to retry on errors before failing the task.";
    private static final String MAX_RETRIES_DISPLAY = "Maximum Retries";

    public static final String RETRY_BACKOFF_MS = "retry.backoff.ms";
    private static final int RETRY_BACKOFF_MS_DEFAULT = 3000;
    private static final String RETRY_BACKOFF_MS_DOC =
      "The time in milliseconds to wait following an error before a retry attempt is made.";
    private static final String RETRY_BACKOFF_MS_DISPLAY = "Retry Backoff (millis)";

    public static final String DATA_FIELD = "data.field";
    private static final String DATA_FIELD_DEFAULT = "";
    private static final String DATA_FIELD_DOC =
      "The field from the Struct to deliver to the server.";
    private static final String DATA_FIELD_DISPLAY = "Data field to deliver";

    static {
        CONFIG_DEF.define(HOSTNAME, ConfigDef.Type.STRING,
            HOSTNAME_DEFAULT, ConfigDef.Importance.HIGH, HOSTNAME_DOC);
        CONFIG_DEF.define(PORT, ConfigDef.Type.INT,
            PORT_DEFAULT, ConfigDef.Importance.HIGH, PORT_DOC);
        CONFIG_DEF.define(MAX_RETRIES,ConfigDef.Type.INT,
            MAX_RETRIES_DEFAULT,ConfigDef.Importance.MEDIUM,MAX_RETRIES_DOC);
        CONFIG_DEF.define(RETRY_BACKOFF_MS,ConfigDef.Type.INT,
            RETRY_BACKOFF_MS_DEFAULT,ConfigDef.Importance.MEDIUM,RETRY_BACKOFF_MS_DOC);
            CONFIG_DEF.define(DATA_FIELD,ConfigDef.Type.STRING,
                DATA_FIELD_DEFAULT,ConfigDef.Importance.MEDIUM,DATA_FIELD_DOC);
    }

    SocketSinkConfig(final Map<? ,?> props) {
        super(CONFIG_DEF, props);
    }
}
