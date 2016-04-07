package com.messenger.api;

import com.google.gson.annotations.SerializedName;

public class TranslateTextBody {

    private final String text;

    @SerializedName("to")
    private final String toLanguage;

    public TranslateTextBody(String text, String toLanguage) {
        this.text = text;
        this.toLanguage = toLanguage;
    }
}
