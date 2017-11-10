package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.AuthorizedStaticInfoPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.EnrollMemberPresenter;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;

@Layout(R.layout.fragment_webview)
public class EnrollMemberFragment extends AuthorizedStaticInfoFragment<UrlBundle> {

   @Override
   protected String getURL() {
      return provider.getEnrollMemberUrl();
   }

   @Override
   protected AuthorizedStaticInfoPresenter createPresenter(Bundle savedInstanceState) {
      return new EnrollMemberPresenter(getURL());
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      webView.getSettings().setLoadWithOverviewMode(true);
      webView.getSettings().setUseWideViewPort(true);
   }

   @Override
   protected void trackViewFromViewPagerIfNeeded() {
      getPresenter().track(Route.ENROLL_MEMBER);
   }
}