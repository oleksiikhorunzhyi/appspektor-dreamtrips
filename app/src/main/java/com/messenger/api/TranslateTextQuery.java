package com.messenger.api;

import com.worldventures.dreamtrips.core.api.request.Query;

public class TranslateTextQuery extends Query<String> {

    private final TranslateTextBody body;

    public TranslateTextQuery(TranslateTextBody body) {
        super(String.class);
        this.body = body;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        return getService().translateText(body).getTranslatedText();
    }
}
