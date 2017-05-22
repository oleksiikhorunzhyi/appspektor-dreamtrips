package com.worldventures.dreamtrips.modules.background_uploading.service.command.video.http;


import android.text.TextUtils;

import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoProcessBunchStatus;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;
import io.techery.janet.http.annotations.Url;

import static io.techery.janet.http.annotations.HttpAction.Method.GET;

@HttpAction(method = GET)
public class CheckVideoProcessingHttpAction extends BaseHttpAction {

   @Url
   String url = "http://dev-assetuploadersrv-114966274.us-east-1.elb.amazonaws.com/api/v1/Asset/Status";

   @Query("tempIds")
   public final String ids;

   @Query("memberId")
   public final String memberId;

   @Query("ssoToken")
   public final String ssoToken;

   @Response
   VideoProcessBunchStatus bunchStatus;

   public CheckVideoProcessingHttpAction(List<String> ids, String memberId, String ssoToken) {
      this.ids = TextUtils.join(",", ids);
      this.memberId = memberId;
      this.ssoToken = ssoToken;
   }

   public VideoProcessBunchStatus getBunchStatus() {
      return bunchStatus;
   }
}
