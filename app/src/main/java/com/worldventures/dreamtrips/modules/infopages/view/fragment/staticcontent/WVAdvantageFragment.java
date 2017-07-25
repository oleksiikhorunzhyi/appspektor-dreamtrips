package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.HeaderProvider;
import com.worldventures.dreamtrips.modules.membership.bundle.UrlBundle;

import java.util.HashMap;
import java.util.Map;

@Layout(R.layout.fragment_webview)
public class WVAdvantageFragment extends AuthorizedStaticInfoFragment<UrlBundle> {

   @Override
   protected String getURL() {
      return provider.getWvAdvantageUrl();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      webView.getSettings().setLoadWithOverviewMode(true);
      webView.getSettings().setUseWideViewPort(true);
   }

   @Override
   protected Map<String, String> getAdditionalHeaders() {
      Map<String, String> headers = new HashMap<>();
      headers.put(AUTHORIZATION_HEADER_KEY, getPresenter().getLegacyAuthTokenBase64().replaceAll("\n", ""));
      HeaderProvider.Header header = headerProvider.getApplicationIdentifierHeader();
      headers.put(header.getName(), header.getValue());
      return headers;
   }
}