package com.worldventures.wallet.domain.entity

import com.esotericsoftware.kryo.DefaultSerializer
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer

@DefaultSerializer(CompatibleFieldSerializer::class)
data class AboutSmartCardData(val smartCardFirmware: SmartCardFirmware = SmartCardFirmware())
