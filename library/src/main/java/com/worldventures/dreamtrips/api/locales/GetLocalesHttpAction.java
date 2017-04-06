package com.worldventures.dreamtrips.api.locales;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.locales.responses.Locale;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/locales")
public class GetLocalesHttpAction extends AuthorizedHttpAction {
    @Response
    List<Locale> response;

    public List<Locale> response() {
        return response;
    }
}
