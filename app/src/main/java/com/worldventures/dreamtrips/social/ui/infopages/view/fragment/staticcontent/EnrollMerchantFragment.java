package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantIdBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.MerchantStaticPageProvider;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.AuthorizedStaticInfoPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.EnrollMerchantPresenter;

import javax.inject.Inject;

@Layout(R.layout.fragment_webview)
public class EnrollMerchantFragment extends AuthorizedStaticInfoFragment<MerchantIdBundle> {

   @Inject MerchantStaticPageProvider merchantStaticPageProvider;

   @Override
   protected String getURL() {
      return merchantStaticPageProvider.getEnrollMerchantUrl(getArgs());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      webView.getSettings().setLoadWithOverviewMode(true);
      webView.getSettings().setUseWideViewPort(true);
   }

   @Override
   protected AuthorizedStaticInfoPresenter createPresenter(Bundle savedInstanceState) {
      return new EnrollMerchantPresenter(getURL(), getArgs());
   }

   @Override
   protected void trackViewFromViewPagerIfNeeded() {
      super.trackViewFromViewPagerIfNeeded();
      getPresenter().track(Route.ENROLL_MERCHANT);
   }
}
