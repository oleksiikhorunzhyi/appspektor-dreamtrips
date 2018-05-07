package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.WebViewFragmentPresenter;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;

@Layout(R.layout.fragment_webview)
public abstract class BundleUrlFragment<T extends WebViewFragmentPresenter> extends StaticInfoFragment<T, UrlBundle> {

   @Override
   public void afterCreateView(View rootView) {
      webView.getSettings().setDomStorageEnabled(true);
      super.afterCreateView(rootView);
   }
}
