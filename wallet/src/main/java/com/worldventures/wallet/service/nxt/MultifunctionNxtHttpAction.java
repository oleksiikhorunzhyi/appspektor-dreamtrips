package com.worldventures.wallet.service.nxt;

import com.worldventures.wallet.service.nxt.model.MultiErrorResponse;
import com.worldventures.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.wallet.service.nxt.model.MultiResponseBody;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Status;

import static io.techery.janet.http.annotations.Response.ERROR;

@HttpAction(method = HttpAction.Method.POST, value = "/multifunction")
public class MultifunctionNxtHttpAction {

   static final int MULTIFUNCTION_REQUEST_ELEMENTS_LIMIT = 40;

   private static final String CONTENT_TYPE = "application/json";

   @RequestHeader("Content-Type") final String contentTypeHeader;

   @Body MultiRequestBody body;

   @Response MultiResponseBody response;
   @Response(value = ERROR) MultiErrorResponse errorResponse;
   @Status int statusCode;

   public MultifunctionNxtHttpAction(MultiRequestBody body) {
      contentTypeHeader = CONTENT_TYPE;

      this.body = body;
   }

   public MultiResponseBody getResponse() {
      return response;
   }

   public MultiErrorResponse getErrorResponse() {
      return errorResponse;
   }

   public int getStatusCode() {
      return statusCode;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MultifunctionNxtHttpAction that = (MultifunctionNxtHttpAction) o;
      return body != null ? body.equals(that.body) : that.body == null;
   }

   @Override
   public int hashCode() {
      return body != null ? body.hashCode() : 0;
   }
}