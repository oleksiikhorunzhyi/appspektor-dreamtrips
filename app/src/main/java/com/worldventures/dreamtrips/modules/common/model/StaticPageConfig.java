package com.worldventures.dreamtrips.modules.common.model;

import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

public class StaticPageConfig {

    @SerializedName("Documents")
    private List<Document> documents;

    public String getUrlByTitle(String title) {
        String result = "";
        if (documents != null) {
            Document document = Queryable.from(documents).where(temp ->
                    title.equalsIgnoreCase(temp.nameNative)).first();
            if (document != null) {
                result = document.url;
            }
        }
        return result;
    }

    private class Document {
        @SerializedName("CountryCode")
        private String countryCode;
        @SerializedName("LanguageCode")
        private String languageCode;
        @SerializedName("Name")
        private String name;
        @SerializedName("NameNative")
        private String nameNative;
        @SerializedName("Type")
        private String type;
        @SerializedName("Url")
        private String url;

    }
}
