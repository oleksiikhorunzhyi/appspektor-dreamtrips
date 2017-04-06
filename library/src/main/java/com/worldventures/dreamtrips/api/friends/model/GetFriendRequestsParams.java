package com.worldventures.dreamtrips.api.friends.model;

import com.worldventures.dreamtrips.api.api_common.model.PaginatedParams;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Gson.TypeAdapters
@Value.Immutable
public interface GetFriendRequestsParams extends PaginatedParams {
}
