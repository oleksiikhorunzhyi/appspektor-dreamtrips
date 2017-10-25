package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.AuthorizedStaticInfoPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.EnrollRepPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.service.analytics.EnrolRepViewedAction;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;

@Layout(R.layout.fragment_webview)
public class EnrollRepFragment extends AuthorizedStaticInfoFragment<UrlBundle> {

   @Override
   protected String getURL() {
      return provider.getEnrollRepUrl();
   }

   @Override
   protected AuthorizedStaticInfoPresenter createPresenter(Bundle savedInstanceState) {
      return new EnrollRepPresenter(getURL());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      webView.getSettings().setLoadWithOverviewMode(true);
      webView.getSettings().setUseWideViewPort(true);
   }

   @Override
   protected void sendPageDisplayedAnalyticsEvent() {
      analyticsInteractor.analyticsActionPipe().send(new EnrolRepViewedAction());
   }

}