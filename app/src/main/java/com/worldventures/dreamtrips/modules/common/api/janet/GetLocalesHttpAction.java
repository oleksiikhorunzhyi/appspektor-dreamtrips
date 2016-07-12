package com.worldventures.dreamtrips.modules.common.api.janet;

import com.worldventures.dreamtrips.core.api.action.AuthorizedHttpAction;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;

import java.util.List;

import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Response;

@HttpAction(value = "/api/locales", method = HttpAction.Method.GET)
public class GetLocalesHttpAction extends AuthorizedHttpAction {

    @Response
    List<AvailableLocale> availableLocales;

    public List<AvailableLocale> getAvailableLocales() {
        return availableLocales;
    }

}
