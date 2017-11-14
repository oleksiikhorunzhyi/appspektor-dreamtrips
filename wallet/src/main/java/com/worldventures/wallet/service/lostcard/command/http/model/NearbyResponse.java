package com.worldventures.wallet.service.lostcard.command.http.model;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Gson.TypeAdapters
public interface NearbyResponse {

   List<ApiPlace> locationPlaces();
}
