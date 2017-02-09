package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.Response;

@HttpAction(method = HttpAction.Method.POST, value = "/multifunction")
public class MultifunctionNxtHttpAction {

   private static final String CONTENT_TYPE = "application/json";

   @RequestHeader("Content-Type") final String contentTypeHeader;

   @Body final MultiRequestBody body;

   @Response MultiResponseBody response;

   public MultifunctionNxtHttpAction(MultiRequestBody body) {
      contentTypeHeader = CONTENT_TYPE;

      this.body = body;
   }

   public MultiResponseBody getResponse() {
      return response;
   }

}