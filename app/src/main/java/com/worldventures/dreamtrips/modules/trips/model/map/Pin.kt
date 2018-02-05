package com.worldventures.dreamtrips.modules.trips.model.map

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer

@DefaultSerializer(CompatibleFieldSerializer::class)
data class Pin(val coordinates: Coordinates = Coordinates(), val tripUids: List<String> = emptyList())
