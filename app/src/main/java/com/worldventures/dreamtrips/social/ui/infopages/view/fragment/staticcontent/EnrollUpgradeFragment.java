package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.support.annotation.NonNull;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.EnrollUpgradePresenter;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;

import java.util.Map;

@Layout(R.layout.fragment_webview)
public class EnrollUpgradeFragment extends AuthorizedStaticInfoFragment<EnrollUpgradePresenter, UrlBundle> {

   @Override
   public void reload(@NonNull String url, Map<String, String> headers) {
      webView.loadUrl("about:blank");
      load(url, headers);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      webView.getSettings().setLoadWithOverviewMode(true);
      webView.getSettings().setUseWideViewPort(true);
   }
}
