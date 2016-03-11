package com.messenger.api;

import com.google.gson.annotations.SerializedName;

public class TranslateTextBody {

    private final String text;

    @SerializedName("from")
    private final String fromLanguage;

    @SerializedName("to")
    private final String toLanguage;

    public TranslateTextBody(String text, String fromLanguage, String toLanguage) {
        this.text = text;
        this.fromLanguage = fromLanguage;
        this.toLanguage = toLanguage;
    }
}
