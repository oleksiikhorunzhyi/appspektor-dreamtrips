package com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Url;

import static io.techery.janet.http.annotations.HttpAction.Method.DELETE;

@HttpAction(method = DELETE)
public class AbortVideoUploadingHttpAction {

   @Url String url;

   public AbortVideoUploadingHttpAction(String url) {
      this.url = url;
   }
}
