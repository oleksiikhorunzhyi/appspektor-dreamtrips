package com.worldventures.dreamtrips.modules.common.model;

import com.google.gson.annotations.SerializedName;
import com.innahema.collections.query.queriables.Queryable;

import java.util.List;

public class StaticPageConfig {

    @SerializedName("Documents")
    private List<Document> documents;

    public String getUrlByTitle(String title) {
        String result = "";
        if (documents != null && documents.size() > 0) {
            Document document = Queryable.from(documents).where(temp ->
                    title.equalsIgnoreCase(temp.name)).first();
            if (document != null) {
                result = document.url;
            }
        }
        return result;
    }

    private class Document {
        @SerializedName("CountryCode")
        protected String countryCode;
        @SerializedName("LanguageCode")
        protected String languageCode;
        @SerializedName("Name")
        protected String name;
        @SerializedName("NameNative")
        protected String nameNative;
        @SerializedName("Type")
        protected String type;
        @SerializedName("Url")
        protected String url;

    }
}
