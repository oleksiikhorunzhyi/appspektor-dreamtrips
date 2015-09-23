package com.worldventures.dreamtrips.modules.gcm.model;

import com.innahema.collections.query.queriables.Queryable;

public enum PushType {

    ACCEPT_REQUEST, SEND_REQUEST, UNKNOWN;

    public static PushType of(String type) {
        PushType result = Queryable.from(values())
                .firstOrDefault(element -> element.name().equalsIgnoreCase(type));
        return result == null ? UNKNOWN : result;
    }
}
