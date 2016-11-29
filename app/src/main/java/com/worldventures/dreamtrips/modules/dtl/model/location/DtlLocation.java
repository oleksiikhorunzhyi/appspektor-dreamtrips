package com.worldventures.dreamtrips.modules.dtl.model.location;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.locations.model.LocationType;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import org.immutables.value.Value;

import java.util.List;
import java.util.Locale;

@Value.Immutable
public abstract class DtlLocation {

   public abstract LocationSourceType locationSourceType();

   @Nullable public abstract String id();

   @Nullable public abstract String longName();

   @Nullable public abstract LatLng coordinates();

   @Nullable public abstract List<DtlLocation> locatedIn();

   @Nullable public abstract LocationType type();

   @Value.Default public boolean isExternal() {
      return true;
   }

   @Value.Default public String analyticsName() {
      if(isExternal() && locatedIn() != null) return String.format("%s:%s:%s", longName(), getLongNameFor(LocationType.STATE), getLongNameFor(LocationType.COUNTRY));
      else return "-:-:-";
   }

   @Value.Derived public String provideFormattedLocation() {
      if(coordinates() == null) return "";
      return String.format(Locale.US, "%1$f,%2$f", coordinates().latitude, coordinates().longitude);
   }

   @Value.Derived public boolean isOutOfMinDistance(android.location.Location location) {
      if (coordinates() == null) return false;
      LatLng coordinates = DtlLocationHelper.asLatLng(location);
      return !DtlLocationHelper.checkMinDistance(coordinates(), coordinates);
   }

   @Value.Derived public boolean isOutOfMaxDistance(LatLng location) {
      if (coordinates() == null) return false;
      return !DtlLocationHelper.checkMaxDistance(coordinates(), location);
   }

   public static DtlLocation undefined() {
      return ImmutableDtlLocation.builder()
            .locationSourceType(LocationSourceType.UNDEFINED)
            .isExternal(false)
            .build();
   }

   private String getLongNameFor(LocationType type) {
      DtlLocation location = Queryable.from(locatedIn())
            .firstOrDefault(tempLocation -> tempLocation.type() == type);
      return location == null ? "-" : location.longName();
   }
}
