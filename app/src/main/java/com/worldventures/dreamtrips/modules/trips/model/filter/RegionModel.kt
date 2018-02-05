package com.worldventures.dreamtrips.modules.trips.model.filter

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import java.io.Serializable

@DefaultSerializer(CompatibleFieldSerializer::class)
data class RegionModel(val id: Int = 0, val name: String = "", var isChecked: Boolean = true) : Serializable
