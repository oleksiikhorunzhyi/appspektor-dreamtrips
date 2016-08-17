package com.worldventures.dreamtrips.modules.dtl.model.location;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import org.immutables.value.Value;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
@Value.Style(privateNoargConstructor = true)
public abstract class DtlManualLocation implements DtlLocation {

   protected DtlManualLocation() {}

   ;

   @Override
   public abstract LocationSourceType getLocationSourceType();

   @Override
   @Nullable
   public abstract String getLongName();

   @Override
   public abstract Location getCoordinates();

   @Override
   @Value.Default
   public String getAnalyticsName() {
      return "-:-:-";
   }
}
