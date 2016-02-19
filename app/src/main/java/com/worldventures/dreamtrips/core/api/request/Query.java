package com.worldventures.dreamtrips.core.api.request;

import android.content.Context;

public abstract class Query<T> extends DreamTripsRequest<T> {
    public Query(Class<T> clazz) {
        super(clazz);
    }
}
