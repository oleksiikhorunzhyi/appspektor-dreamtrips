package com.worldventures.dreamtrips.modules.config.model

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer

@DefaultSerializer(CompatibleFieldSerializer::class)
data class Configuration(val updateRequirement: UpdateRequirement? = null,
                         val videoRequirement: VideoRequirement = VideoRequirement(),
                         val travelBannerRequirement: TravelBannerRequirement? = null)
