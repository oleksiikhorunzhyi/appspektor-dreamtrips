package com.worldventures.dreamtrips.modules.config.model

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer

@DefaultSerializer(CompatibleFieldSerializer::class)
data class UpdateRequirement(val appVersion: String = "", val timeStamp: Long = 0)
