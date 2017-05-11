package com.worldventures.dreamtrips.modules.trips.model.filter;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
public class CachedTripFilters implements Serializable {

   private List<ActivityModel> activities = new ArrayList<>();
   private List<RegionModel> regions = new ArrayList<>();

   public CachedTripFilters() {
   }

   public CachedTripFilters(List<RegionModel> regions, List<ActivityModel> activities) {
      this.activities = activities;
      this.regions = regions;
   }

   public List<ActivityModel> getActivities() {
      return activities;
   }

   public List<RegionModel> getRegions() {
      return regions;
   }
}
