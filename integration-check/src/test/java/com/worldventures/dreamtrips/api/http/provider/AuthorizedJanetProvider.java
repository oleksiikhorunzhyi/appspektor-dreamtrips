package com.worldventures.dreamtrips.api.http.provider;

import com.worldventures.dreamtrips.api.http.service.DreamTripsAuthService;
import com.worldventures.dreamtrips.api.session.model.Session;

public class AuthorizedJanetProvider extends BaseJanetProvider<DreamTripsAuthService> {

    public AuthorizedJanetProvider() {
        super(new AuthorizedHttpServiceProvider(new SystemEnvProvider().provide()).provide());
    }

    public Session session() {
        return getService().getSession();
    }
}
