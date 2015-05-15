package com.worldventures.dreamtrips.modules.common.model;

import com.google.gson.annotations.SerializedName;

public class AvailableLocale {

    @SerializedName("locale_name")
    private String locale;
    private String country;
    private String language;

    public String getLocale() {
        return locale;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }
}
