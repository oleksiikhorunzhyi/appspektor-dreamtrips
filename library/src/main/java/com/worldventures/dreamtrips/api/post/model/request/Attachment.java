package com.worldventures.dreamtrips.api.post.model.request;

import com.worldventures.dreamtrips.api.api_common.model.UniqueIdentifiable;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import static org.immutables.value.Value.Style.ImplementationVisibility.PACKAGE;

@Gson.TypeAdapters
@Value.Immutable
@Value.Style(visibility = PACKAGE)
public abstract class Attachment implements UniqueIdentifiable {

    public static Attachment of(String uid) {
        return ImmutableAttachment.builder().uid(uid).build();
    }

}
