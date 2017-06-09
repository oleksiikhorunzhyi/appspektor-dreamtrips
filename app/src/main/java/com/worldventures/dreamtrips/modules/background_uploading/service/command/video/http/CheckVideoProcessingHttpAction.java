package com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http;


import android.text.TextUtils;

import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoProcessBunchStatus;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "api/v1/Asset/Status")
public class CheckVideoProcessingHttpAction extends BaseVideoHttpAction {

   @Query("memberId") String memberId;
   @Query("ssoToken") String ssoToken;
   @Query("assetIds") String ids;

   @Response VideoProcessBunchStatus bunchStatus;

   public CheckVideoProcessingHttpAction(List<String> ids) {
      this.ids = TextUtils.join(",", ids);
   }

   public VideoProcessBunchStatus getBunchStatus() {
      return bunchStatus;
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
