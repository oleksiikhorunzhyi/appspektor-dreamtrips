package com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent;

import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import java.util.HashMap;
import java.util.Map;

public abstract class LanguageHeaderStaticInfoFragment extends StaticInfoFragment {

   protected static final String ACCEPT_LANGUAGE_HEADER_KEY = "Accept-Language";

   @Override
   protected Map<String, String> getAdditionalHeaders() {
      Map<String, String> additionalHeaders = new HashMap<>();
      additionalHeaders.put(ACCEPT_LANGUAGE_HEADER_KEY, LocaleHelper.getDefaultLocaleFormatted());
      return additionalHeaders;
   }
}
