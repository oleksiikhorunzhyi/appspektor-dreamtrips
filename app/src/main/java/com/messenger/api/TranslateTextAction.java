package com.messenger.api;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/translate", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.POST)
public class TranslateTextAction extends AuthorizedHttpAction {

    @Body TranslateTextBody body;
    @Response TranslatedText translatedText;

    public TranslateTextAction(String text, String toLanguage) {
        this.body = new TranslateTextBody(text, toLanguage);
    }

    public String getTranslatedText() {
        return translatedText.getTranslatedText();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private class TranslateTextBody {
        private final String text;

        @SerializedName("to")
        private final String toLanguage;

        public TranslateTextBody(String text, String toLanguage) {
            this.text = text;
            this.toLanguage = toLanguage;
        }
    }

}
