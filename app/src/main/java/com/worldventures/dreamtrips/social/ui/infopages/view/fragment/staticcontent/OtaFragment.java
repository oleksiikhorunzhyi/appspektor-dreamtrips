package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.OtaPresenter;
import com.worldventures.dreamtrips.social.ui.membership.bundle.UrlBundle;

import java.util.Map;

@Layout(R.layout.fragment_webview_with_overlay)
@MenuResource(R.menu.menu_mock)
public class OtaFragment extends AuthorizedStaticInfoFragment<OtaPresenter, UrlBundle> {

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      getPresenter().sendAnalyticsOtaViewedAction();
   }

   @Override
   protected OtaPresenter createPresenter(Bundle savedInstanceState) {
      return new OtaPresenter();
   }

   @Override
   public void reload(@NonNull String url, Map<String, String> headers) {
      webView.loadUrl(BLANK_PAGE);
      load(url, headers);
   }
}
