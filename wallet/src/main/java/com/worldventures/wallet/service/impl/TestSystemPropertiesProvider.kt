package com.worldventures.wallet.service.impl

import com.worldventures.wallet.service.SystemPropertiesProvider

/**
 * Created by shliama on 1/31/17.
 */
class TestSystemPropertiesProvider : SystemPropertiesProvider {

   override fun deviceId() = ""

   override fun osVersion() = ""

   override fun deviceName() = ""

}
