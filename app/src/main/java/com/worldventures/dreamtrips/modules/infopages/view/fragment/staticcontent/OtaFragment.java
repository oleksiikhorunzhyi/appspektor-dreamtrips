package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.infopages.presenter.OtaPresenter;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;

import java.util.HashMap;
import java.util.Map;

@Layout(R.layout.fragment_webview_with_overlay)
@MenuResource(R.menu.menu_mock)
public class OtaFragment extends AuthorizedStaticInfoFragment<UrlBundle> {

   @Override
   protected String getURL() {
      return provider.getOtaPageUrl();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      getPresenter().track(Route.OTA);
   }

   @Override
   protected OtaPresenter createPresenter(Bundle savedInstanceState) {
      return new OtaPresenter(getURL());
   }

   @Override
   protected Map<String, String> getAdditionalHeaders() {
      Map<String, String> additionalHeaders = new HashMap<>();
      additionalHeaders.put(AUTHORIZATION_HEADER_KEY, getPresenter().getAuthToken());
      return additionalHeaders;
   }

   @Override
   public void reload(String url) {
      webView.loadUrl(BLANK_PAGE);
      load(url);
   }
}
