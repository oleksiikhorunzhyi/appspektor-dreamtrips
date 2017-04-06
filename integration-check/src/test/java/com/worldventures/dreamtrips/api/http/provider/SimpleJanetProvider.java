package com.worldventures.dreamtrips.api.http.provider;

import io.techery.janet.ActionServiceWrapper;

public class SimpleJanetProvider extends BaseJanetProvider<ActionServiceWrapper> {

    public SimpleJanetProvider(ActionServiceWrapper service) {
        super(service);
    }
}
