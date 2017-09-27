package com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video.http;

import com.worldventures.dreamtrips.social.ui.background_uploading.model.video.VideoResponse;

import java.io.File;

import io.techery.janet.body.FileBody;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Part;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.model.MultipartRequestBody;

@HttpAction(value = "api/v1/Async/Asset", method = HttpAction.Method.POST, type = HttpAction.Type.MULTIPART)
public class UploadVideoHttpAction extends BaseVideoHttpAction {

   @Part("MemberId") String memberId;
   @Part("SSOToken") String ssoToken;
   @Part("File") MultipartRequestBody.PartBody fileBody;

   @Response VideoResponse videoResponse;

   public UploadVideoHttpAction(String filePath) {
      this.fileBody = new MultipartRequestBody.PartBody.Builder()
            .setBody(new FileBody("video/*", new File(filePath)))
            .addHeader("filename", "content")
            .build();
   }

   public String getAssetId() {
      return videoResponse == null ? null : videoResponse.getContent().getAssetId();
   }

   @Override
   public void setMemberId(String memberId) {
      this.memberId = memberId;
   }

   @Override
   public void setSsoToken(String ssoToken) {
      this.ssoToken = ssoToken;
   }

   @Override
   public String getSsoToken() {
      return ssoToken;
   }
}
