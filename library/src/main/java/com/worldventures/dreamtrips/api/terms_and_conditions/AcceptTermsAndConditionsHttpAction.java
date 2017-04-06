package com.worldventures.dreamtrips.api.terms_and_conditions;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;

import io.techery.janet.http.annotations.Body;
import io.techery.janet.http.annotations.HttpAction;

import static io.techery.janet.http.annotations.HttpAction.Method.POST;


@HttpAction(value = "/api/terms_and_conditions/accept", method = POST)
public class AcceptTermsAndConditionsHttpAction extends AuthorizedHttpAction {

    @Body
    public final ActionBody body;

    public AcceptTermsAndConditionsHttpAction(String text) {
        this.body = new ActionBody(text);
    }

    private static class ActionBody {
        @SerializedName("text")
        public final String text;

        private ActionBody(String text) {
            this.text = text;
        }
    }
}
