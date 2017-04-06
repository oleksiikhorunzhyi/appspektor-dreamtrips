package com.worldventures.dreamtrips.api.documents.model;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface Document {
    int id();

    String name();

    String url();
}
