package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;

import java.util.HashMap;
import java.util.Map;

@Layout(R.layout.fragment_webview)
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
   protected void sendAnalyticEvent(String actionAnalyticEvent) {
      TrackingHelper.actionBookTravelScreen(actionAnalyticEvent);
   }

   @Override
   public void load(String url) {
      if (!isLoading && savedState == null) {
         Map<String, String> additionalHeaders = new HashMap<>();
         additionalHeaders.put(AUTHORIZATION_HEADER_KEY, getPresenter().getAuthToken());
         webView.loadUrl(url, additionalHeaders);
      }
   }

   @Override
   public void reload(String url) {
      webView.loadUrl("about:blank");
      load(url);
   }
}
