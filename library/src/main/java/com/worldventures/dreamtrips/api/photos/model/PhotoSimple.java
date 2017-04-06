package com.worldventures.dreamtrips.api.photos.model;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface PhotoSimple extends PhotoWithAuthor {

}
