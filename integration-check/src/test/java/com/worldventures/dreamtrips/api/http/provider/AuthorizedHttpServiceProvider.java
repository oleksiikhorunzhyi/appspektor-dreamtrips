package com.worldventures.dreamtrips.api.http.provider;

import com.worldventures.dreamtrips.api.http.EnvParams;
import com.worldventures.dreamtrips.api.http.service.DreamTripsAuthService;

public class AuthorizedHttpServiceProvider extends SimpleHttpServiceProvider {

    public AuthorizedHttpServiceProvider(EnvParams envParams) {
        super(envParams);
    }

    @Override
    public DreamTripsAuthService provide() {
        return new DreamTripsAuthService(super.provide());
    }
}
