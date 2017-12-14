package com.worldventures.dreamtrips.modules.config.model

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer

@DefaultSerializer(CompatibleFieldSerializer::class)
data class VideoRequirement(val videoMaxLength: Int = DEFAULT_MAX_VIDEO_DURATION_SEC) {

   companion object {
      val DEFAULT_MAX_VIDEO_DURATION_SEC = 90
   }
}
