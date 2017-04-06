package com.worldventures.dreamtrips.api.entity;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;
import com.worldventures.dreamtrips.api.entity.model.EntityHolder;

import io.techery.janet.http.annotations.Path;
import io.techery.janet.http.annotations.Response;

public abstract class GetEntityHttpAction<T extends UniqueIdentifiable> extends AuthorizedHttpAction {

    @Path("uid")
    public final String uid;

    @Response
    EntityHolder<T> entityHolder;

    public GetEntityHttpAction(String uid) {
        this.uid = uid;
    }

    public T response() {
        return entityHolder.entity();
    }
}
