package com.worldventures.wallet.domain.entity

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer

@DefaultSerializer(CompatibleFieldSerializer::class)
data class SmartCardFirmware(
      val firmwareBundleVersion: String? = null,
      val nordicAppVersion: String = "",
      val nrfBootloaderVersion: String = "",
      val internalAtmelVersion: String = "",
      val internalAtmelBootloaderVersion: String = "",
      val externalAtmelVersion: String = "",
      val externalAtmelBootloaderVersion: String = "") {

   val isEmpty: Boolean
      get() = (nordicAppVersion.isEmpty()
            && nrfBootloaderVersion.isEmpty()
            && internalAtmelVersion.isEmpty()
            && externalAtmelVersion.isEmpty())
}
