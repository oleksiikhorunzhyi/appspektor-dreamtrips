package com.worldventures.dreamtrips.api.messenger;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.messenger.model.request.TranslateTextBody;
import com.worldventures.dreamtrips.api.messenger.model.response.TranslatedText;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/translate", type = HttpAction.Type.SIMPLE, method = HttpAction.Method.POST)
public class TranslateTextHttpAction extends AuthorizedHttpAction {

    @Body
    public final TranslateTextBody body;

    @Response
    TranslatedText translatedText;

    public TranslateTextHttpAction(TranslateTextBody translateText) {
        this.body = translateText;
    }

    public String getTranslatedText() {
        return translatedText.text();
    }

}
