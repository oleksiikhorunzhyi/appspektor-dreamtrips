package com.worldventures.dreamtrips.modules.dtl.service.action.bundle;

import android.location.Location;
import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface LocationsActionParams extends HttpActionParams {

   @Nullable String query();

   @Nullable Location location();

}
