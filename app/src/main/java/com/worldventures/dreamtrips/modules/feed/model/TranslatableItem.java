package com.worldventures.dreamtrips.modules.feed.model;

import android.support.annotation.Nullable;

public interface TranslatableItem {

    String getOriginalText();

    @Nullable
    String getTranslation();

    @Nullable
    String getLanguageFrom();

    void setTranslation(String text);

    boolean isTranslated();

    void setTranslated(boolean translated);
}
