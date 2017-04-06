package com.worldventures.dreamtrips.api.http.provider;

import io.techery.janet.ActionService;

public interface HttpServiceProvider<T extends ActionService> {

    T provide();
}
