package com.worldventures.dreamtrips.modules.feed.model;

import android.support.annotation.Nullable;

import java.util.Map;

public interface TranslatableItem {

   Map<String, String> getOriginalText();

   @Nullable
   Map<String, String> getTranslation();

   void setTranslations(Map<String, String> text);

   boolean isTranslated();

   void setTranslated(boolean translated);
}
