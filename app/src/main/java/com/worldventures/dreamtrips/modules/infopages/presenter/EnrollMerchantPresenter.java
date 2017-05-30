package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;

import javax.inject.Inject;

public class EnrollMerchantPresenter extends AuthorizedStaticInfoPresenter {

   @Inject protected StaticPageProvider provider;

   private MerchantIdBundle merchantBundle;

   public EnrollMerchantPresenter(String url, MerchantIdBundle merchantBundle) {
      super(url);
      this.merchantBundle = merchantBundle;
   }

   @Override
   protected void onLoginSuccess() {
      url = provider.getEnrollMerchantUrl(merchantBundle);
      super.onLoginSuccess();
   }
}
