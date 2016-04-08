package com.messenger.api;

import com.google.gson.annotations.SerializedName;

public class TranslatedText {

    @SerializedName("text")
    private String translatedText;


    public String getTranslatedText() {
        return translatedText;
    }
}
