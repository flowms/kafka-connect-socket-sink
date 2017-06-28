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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class SocketSinkTask extends SinkTask {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(SocketSinkTask.class);

    private Socket clientSocket;
    private PrintStream outputStream;

    @Override
    public void flush(final Map<TopicPartition, OffsetAndMetadata> offsets) {
        outputStream.flush();
    }

    @Override
    public void put(final Collection<SinkRecord> records) {
        for (final SinkRecord record : records) {
            outputStream.println(record.value()); 
            if (outputStream.checkError()) {
                throw new RetriableException("Connection closed");
            }
        }
    }

    @Override
    public void start(final Map<String, String> args) {
        final String hostname = args.get(SocketSinkConfig.HOSTNAME);
        LOGGER.info("{} = {}", SocketSinkConfig.HOSTNAME, hostname); 

        final int port = Integer.parseInt(args.get(SocketSinkConfig.PORT));
        LOGGER.info("{} = {}", SocketSinkConfig.PORT, port); 

        try {
            clientSocket = new Socket(hostname, port);

            outputStream = new PrintStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            LOGGER.error("Error connecting to Unix socket {}", e);
            throw new RetriableException("Error connecting to Unix socket" ,e);
        }
    }

    @Override
    public void stop() {
        if (outpuStream != null)
            outputStream.close();
    }

    @Override
    public String version() {
        return Version.getVersion();
    }
}
