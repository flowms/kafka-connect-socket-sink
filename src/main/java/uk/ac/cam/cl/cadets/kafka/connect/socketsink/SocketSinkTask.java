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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;


import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.data.Struct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class SocketSinkTask extends SinkTask {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(SocketSinkTask.class);

    private Socket clientSocket;
    private PrintStream outputStream;

    private int maxRetries;
    private int retryBackoffMs;
    private String hostname;
    private int port;
    int remainingRetries;
    int SocketOpen;
    private String dataField;

    @Override
    public void flush(final Map<TopicPartition, OffsetAndMetadata> offsets) {
        outputStream.flush();
    }

    @Override
    public void put(final Collection<SinkRecord> records) {
        if (records.size() >= 1)
            initWriter(); /* Establish connection every time to handle VPN disconnects not recognized y client and server */
        for (final SinkRecord record : records) {
            final Struct valueStruct = (Struct) record.value();
            /* outputStream.println(dataField == null ? record.value() : valueStruct.get(dataField)); */
            /* write data encapsulated by STX and ETX */
            outputStream.printf("%c%s%c", 2, dataField == null ? record.value() : valueStruct.get(dataField), 3);
            if (outputStream.checkError()) {
                LOGGER.warn(
                  "Write of {} records failed, remainingRetries={}",
                  records.size(),
                  remainingRetries
                  );
                if (remainingRetries == 0) {
                    throw new ConnectException("Connection closed");
                } else {
                    stop();
                    LOGGER.warn(
                      "Socket Connection Closed..."
                      );
                    try
                      {
                        Thread.sleep(retryBackoffMs);
                      }
                      catch(InterruptedException ex)
                      {
                        Thread.currentThread().interrupt();
                      }
                    initWriter();
                    LOGGER.warn(
                      "Socket Initiated..."
                      );

                    remainingRetries--;
                    throw new RetriableException("Recovering from socket write issue, retrying...");
                }
            }
            remainingRetries = maxRetries;
        }
    }

    @Override
    public void start(final Map<String, String> args) {
        hostname = args.get(SocketSinkConfig.HOSTNAME);
        LOGGER.info("{} = {}", SocketSinkConfig.HOSTNAME, hostname);

        port = Integer.parseInt(args.get(SocketSinkConfig.PORT));
        LOGGER.info("{} = {}", SocketSinkConfig.PORT, port);

        maxRetries = Integer.parseInt(args.get(SocketSinkConfig.MAX_RETRIES));
        LOGGER.info("{} = {}", SocketSinkConfig.MAX_RETRIES, maxRetries);

        retryBackoffMs = Integer.parseInt(args.get(SocketSinkConfig.RETRY_BACKOFF_MS));
        LOGGER.info("{} = {}", SocketSinkConfig.RETRY_BACKOFF_MS, retryBackoffMs);

        dataField = args.get(SocketSinkConfig.DATA_FIELD);
        LOGGER.info("{} = {}", SocketSinkConfig.DATA_FIELD, dataField);

        remainingRetries = maxRetries;
        SocketOpen = 0;
        initWriter();
    }

    void initWriter() {
        try {
          stop();
          clientSocket = new Socket();
          clientSocket.connect(new InetSocketAddress(hostname,port),retryBackoffMs);
          clientSocket.setKeepAlive(true);
          clientSocket.setTcpNoDelay(true);
          clientSocket.setSoTimeout(retryBackoffMs);

          outputStream = new PrintStream(clientSocket.getOutputStream());

          SocketOpen = 1;

          LOGGER.info(
          "Initiated Socket Connection to {}:{}...",
          hostname,
          port
          );

        } catch (IOException e) {
          LOGGER.warn(
          "Connection Refused, remainingRetries={}",
          remainingRetries
          );

          if (remainingRetries == 0) {
            throw new ConnectException("Connection Refused - Out of Retries...");
          } else {
            stop();
            try
              {
                Thread.sleep(retryBackoffMs);
              }
              catch(InterruptedException ex)
              {
                Thread.currentThread().interrupt();
              }
              remainingRetries--;
              throw new RetriableException("Connection refused, retrying...");
            }
          }
    }

    @Override
    public void stop() {
        try {
            if (outputStream != null)
                outputStream.flush();
            if (SocketOpen==1)
              clientSocket.close();
            SocketOpen = 0;
        } catch (Exception ex) {
          LOGGER.warn(
          "Problem Closing Socket Connection..."
          );
        }
        LOGGER.info(
        "Stopped Socket Connection..."
        );
    }

    @Override
    public String version() {
        return Version.getVersion();
    }
}
