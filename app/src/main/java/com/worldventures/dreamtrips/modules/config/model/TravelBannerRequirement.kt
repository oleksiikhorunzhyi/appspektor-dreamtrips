package com.worldventures.dreamtrips.modules.config.model

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer

@DefaultSerializer(CompatibleFieldSerializer::class)
data class TravelBannerRequirement(val title: String = "", val url: String = "", val enabled: Boolean = false)
