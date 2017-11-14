package com.worldventures.dreamtrips.modules.dtl.helper;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.modules.infopages.StaticPageProviderConfig;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;

public class MerchantStaticPageProvider {

   private final SessionHolder appSessionHolder;
   private final String apiUrl;

   public MerchantStaticPageProvider(StaticPageProviderConfig config) {
      this.appSessionHolder = config.appSessionHolder();
      this.apiUrl = config.apiUrl();
   }

   public String getEnrollMerchantUrl(MerchantIdBundle args) {
      StringBuilder builder = new StringBuilder(apiUrl);
      UserSession userSession = appSessionHolder.get().get();
      builder.append("/gateway/dtl/enroll_merchant")
            .append("?username=")
            .append(userSession.user().getUsername())
            .append("&sso=")
            .append(userSession.legacyApiToken())
            .append("&locale=")
            .append(userSession.locale());
      //
      if (args != null) {
         builder.append("&intent=suggestProspect").append("&prospectId=").append(args.getMerchantId());
      }
      return builder.toString();
   }
}
