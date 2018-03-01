package com.worldventures.dreamtrips.modules.trips.model.filter

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import java.io.Serializable

@DefaultSerializer(CompatibleFieldSerializer::class)
data class CachedTripFilters(val activities: List<ActivityModel> = emptyList(),
                             val regions: List<RegionModel> = emptyList()) : Serializable
