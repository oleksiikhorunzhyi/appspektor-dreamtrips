package com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http;

import java.io.File;
import java.io.IOException;

import io.techery.janet.body.FileBody;
import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.ResponseHeader;
import io.techery.janet.http.annotations.Url;

import static io.techery.janet.http.annotations.HttpAction.Method.PUT;
import static io.techery.janet.http.annotations.HttpAction.Type.SIMPLE;

@HttpAction(type = SIMPLE, method = PUT)
public class UploadVideoChunkHttpAction {

   @Url final String url;

   @Body
   final FileBody fileBody;

   @RequestHeader("Content-Type")
   String contentType = "video/mp4";

   @ResponseHeader("ETag")
   String eTag;

   public UploadVideoChunkHttpAction(String url, File videoFileChunk) throws IOException {
      this.url = url;
      fileBody = new FileBody("video/*", videoFileChunk);
   }

   public String getETag() {
      return eTag;
   }
}
