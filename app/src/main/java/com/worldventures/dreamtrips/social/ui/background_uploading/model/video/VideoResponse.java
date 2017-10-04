package com.worldventures.dreamtrips.social.ui.background_uploading.model.video;

public class VideoResponse {
   int status;
   String message;
   ResponseContent content;

   public VideoResponse(int status, String message) {
      this.status = status;
      this.message = message;
   }

   public ResponseContent getContent() {
      return content;
   }
}
