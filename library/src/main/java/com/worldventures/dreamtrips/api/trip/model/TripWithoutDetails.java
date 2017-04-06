package com.worldventures.dreamtrips.api.trip.model;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters(nullAsDefault = true)
@Value.Immutable
public abstract class TripWithoutDetails extends Trip {

}
