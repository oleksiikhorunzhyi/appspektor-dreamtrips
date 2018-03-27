package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.WVAdvantagePresenter;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;

@Layout(R.layout.fragment_webview)
public class WVAdvantageFragment extends AuthorizedStaticInfoFragment<WVAdvantagePresenter, UrlBundle> {

   @Override
   protected WVAdvantagePresenter createPresenter(Bundle savedInstanceState) {
      return new WVAdvantagePresenter();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      webView.getSettings().setLoadWithOverviewMode(true);
      webView.getSettings().setUseWideViewPort(true);
   }
}
