package com.worldventures.dreamtrips.modules.dtl.service.action.http;

import com.worldventures.core.janet.BaseThirdPartyHttpAction;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.BuildConfig;

import java.io.File;
import java.util.Locale;

import io.techery.janet.body.FileBody;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Part;
import io.techery.janet.http.annotations.RequestHeader;
import io.techery.janet.http.annotations.Url;

import static io.techery.janet.http.annotations.HttpAction.Type.MULTIPART;

@HttpAction(method = HttpAction.Method.POST, type = MULTIPART)
public class SendEmailHttpAction extends BaseThirdPartyHttpAction {

   @Url String url = BuildConfig.TRANSACTIONS_API_URL.concat("merchants/%s/transactions/%s/email-send-requests");

   @RequestHeader("Authorization") String header;

   @Part(value = "photo")
   final FileBody fileBody;

   public SendEmailHttpAction(String merchantId, String transactionId, String imageRoute, String userId, String ssoToken) {
      this.header = "Basic " + ProjectTextUtils.convertToBase64NoWrap(userId + ":" + ssoToken);
      url = String.format(Locale.US, url, merchantId, transactionId);
      fileBody = new FileBody("image/*", new File(imageRoute));
   }
}
