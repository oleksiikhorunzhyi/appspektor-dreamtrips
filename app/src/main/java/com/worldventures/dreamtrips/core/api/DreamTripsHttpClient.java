/*
 * Copyright 2015-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.worldventures.dreamtrips.core.api;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.http.HttpClient;
import com.amazonaws.http.HttpHeader;
import com.amazonaws.http.HttpRequest;
import com.amazonaws.http.HttpResponse;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import timber.log.Timber;

/**
 * An implementation of {@link HttpClient} by {@link HttpURLConnection}. This is
 * the recommended HTTP client in Android. Compared to {@link },
 * it has one limitation. When handling 'Expected 100-continue' header, it only
 * accepts either 100 continue or 417 reject, and throws
 * {@link ProtocolException} on other status code. Such limitation will cause
 * some issue when talking to S3 service. See <a
 * href="http://docs.aws.amazon.com/AmazonS3/latest/API/RESTObjectPUT.html">S3's
 * Put Object API</a> for the requirement of handling 100-continue.
 */
public class DreamTripsHttpClient implements HttpClient {

   private final ClientConfiguration config;

   public DreamTripsHttpClient(ClientConfiguration config) {
      this.config = config;
   }

   @Override
   public HttpResponse execute(HttpRequest request) throws IOException {
      URL url = request.getUri().toURL();
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();

      configureConnection(connection);
      applyHeadersAndMethod(request, connection);
      writeContentToConnection(request, connection);
      return createHttpResponse(request, connection);
   }

   HttpResponse createHttpResponse(HttpRequest request, HttpURLConnection connection) throws IOException {

      String statusText = connection.getResponseMessage();
      int statusCode = connection.getResponseCode();
      InputStream content = connection.getErrorStream();
      if (content == null) {
         // HEAD method doesn't have a body
         if (!request.getMethod().equals("HEAD")) {
            try {
               content = connection.getInputStream();
            } catch (IOException ioe) {
               Timber.e(ioe, "IO Exception");
            }
         }
      }

      HttpResponse.Builder builder = HttpResponse.builder()
            .statusCode(statusCode)
            .statusText(statusText)
            .content(content);
      for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
         // skip null field that stores connection status
         if (header.getKey() == null) {
            continue;
         }
         // No AWS service return a list of header values, so it's safe to
         // take the first one.
         builder.header(header.getKey(), header.getValue().get(0));
      }

      return builder.build();
   }

   /**
    * This is no op.
    */
   @Override
   public void shutdown() {
      // No op
   }

   /**
    * Writes the content (if any) of the request to the passed connection
    *
    * @param request
    * @param connection
    * @throws IOException
    */
   void writeContentToConnection(HttpRequest request, HttpURLConnection connection) throws IOException {
      // Note: if DoOutput is set to true and method is GET, HttpUrlConnection
      // will silently change the method to POST.
      if (request.getContent() != null && request.getContentLength() >= 0) {
         connection.setDoOutput(true);
         // This is for backward compatibility, because
         // setFixedLengthStreamingMode(long) is available in API level 19.
         connection.setFixedLengthStreamingMode((int) request.getContentLength());
         OutputStream os = new BufferedOutputStream(connection.getOutputStream());
         write(request.getContent(), os);
         os.flush();
         os.close();
      }
   }

   HttpURLConnection applyHeadersAndMethod(HttpRequest request, HttpURLConnection connection) throws ProtocolException {
      // add headers
      if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
         for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
            String key = header.getKey();
            // Skip reserved headers for HttpURLConnection
            if (key.equals(HttpHeader.CONTENT_LENGTH) || key.equals(HttpHeader.HOST)) {
               continue;
            }
                /*
                 * Amazon S3 suggests set 100-continue header prior to sending
                 * the request body in order to improve efficiency. S3 may
                 * return '100 Continue' or 417 (Expectation failed). It may
                 * also respond with 307 to redirect the request to the correct
                 * regional location, in which case HttpURLConnection will throw
                 * ProtocolException because it only expects either a 100 or a
                 * 417 response. As a result, this feature is explicitly
                 * disabled. To prevent sending the request body twice due to
                 * redirect, please choose the correct endpoint.
                 */
            if (key.equals(HttpHeader.EXPECT)) {
               // continue;
            }
            connection.setRequestProperty(key, header.getValue());
         }
      }

      String method = request.getMethod();
      connection.setRequestMethod(method);
      return connection;
   }

   private void write(InputStream is, OutputStream os) throws IOException {
      byte[] buf = new byte[1024 * 8];
      int len;
      while ((len = is.read(buf)) != -1) {
         os.write(buf, 0, len);
      }
   }

   void configureConnection(HttpURLConnection connection) {
      // configure the connection
      connection.setConnectTimeout(config.getConnectionTimeout());
      connection.setReadTimeout(config.getSocketTimeout());
      // disable redirect and cache
      connection.setInstanceFollowRedirects(false);
      connection.setUseCaches(false);
      // configure https connection
      if (connection instanceof HttpsURLConnection) {
         HttpsURLConnection https = (HttpsURLConnection) connection;
         https.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

      }
   }

}
