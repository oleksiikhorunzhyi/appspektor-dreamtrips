package com.worldventures.dreamtrips.core.utils;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.List;

import retrofit.client.Header;
import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

public class InterceptingOkClient extends OkClient {

   ResponseHeaderListener responseHeaderListener;

   public InterceptingOkClient(OkHttpClient client) {
      super(client);
   }

   @Override
   public Response execute(Request request) throws IOException {
      Response response = super.execute(request);
      if (responseHeaderListener != null) responseHeaderListener.onResponse(response.getHeaders());
      return response;
   }


   public void setResponseHeaderListener(ResponseHeaderListener responseHeaderListener) {
      this.responseHeaderListener = responseHeaderListener;
   }

   public interface ResponseHeaderListener {
      void onResponse(List<Header> header);
   }
}