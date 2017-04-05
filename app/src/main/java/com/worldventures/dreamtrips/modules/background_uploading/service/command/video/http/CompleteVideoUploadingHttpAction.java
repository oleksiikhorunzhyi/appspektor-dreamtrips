package com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http;

import com.amazonaws.services.s3.internal.XmlWriter;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.ChunkUploadingStatus;


import java.util.List;

import io.techery.janet.body.StringBody;
import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Status;
import io.techery.janet.http.annotations.Url;
import timber.log.Timber;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;

@HttpAction(method = POST)
public class CompleteVideoUploadingHttpAction {

   @Url String url;
   @Body StringBody stringBody;

   @RequestHeader("Content-Type")
   String contentType = "applicatiom/xml";

   @Status int statusCode;
   @Response(min = 400, max = 499) String errorResponse;
   @Response String xmlResponse;

   public CompleteVideoUploadingHttpAction(String url, List<ChunkUploadingStatus> chunkUploadingStatuses) {
      this.url = url;
      this.stringBody = new StringBody(composeCompleteXmlBody(chunkUploadingStatuses));
      Timber.d(stringBody.getString());
   }

   public String getErrorResponse() {
      return errorResponse;
   }

   private String composeCompleteXmlBody(List<ChunkUploadingStatus> chunkUploadingStatuses) {
      XmlWriter xmlWriter = new XmlWriter();
      xmlWriter.start("CompleteMultipartUpload");
      for (ChunkUploadingStatus chunkUploadingStatus : chunkUploadingStatuses) {
         xmlWriter.start("Part")
               .start("PartNumber").value((chunkUploadingStatus.chunkNumber() + 1) + "").end()
               .start("ETag").value(chunkUploadingStatus.eTag().replaceAll("\"", "")).end()
               .end();
      }
      xmlWriter.end();
      return xmlWriter.toString();
   }

   public String getResponse() {
      int posStartUrl = xmlResponse.indexOf("<Location>");
      int posEndUrl = xmlResponse.indexOf("</Location>");
      if (posStartUrl != -1 & posEndUrl != -1) {
         posStartUrl += 10;
         return xmlResponse.substring(posStartUrl, posEndUrl);
      } else return null;
   }

}
