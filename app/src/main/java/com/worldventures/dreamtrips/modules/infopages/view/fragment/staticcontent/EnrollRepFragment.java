package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.infopages.presenter.AuthorizedStaticInfoPresenter;
import com.worldventures.dreamtrips.modules.infopages.presenter.EnrollRepPresenter;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;

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
   protected void sendAnalyticEvent(String actionAnalyticEvent) {
      TrackingHelper.actionRepToolsEnrollment(actionAnalyticEvent);
   }

}