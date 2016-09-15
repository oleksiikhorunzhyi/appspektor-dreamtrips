package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public abstract class LanguageHeaderStaticInfoFragment extends StaticInfoFragment {

   protected static final String ACCEPT_LANGUAGE_HEADER_KEY = "Accept-Language";

   @Inject LocaleHelper localeHelper;

   @Override
   public void load(String url) {
      if (!isLoading && savedState == null) {
         Map<String, String> additionalHeaders = new HashMap<>();
         additionalHeaders.put(ACCEPT_LANGUAGE_HEADER_KEY, localeHelper.getDefaultLocaleFormatted());
         webView.loadUrl(url, additionalHeaders);
      }
   }
}
