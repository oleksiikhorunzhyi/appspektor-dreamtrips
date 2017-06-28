package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone

class TestSmartCardUserPhone : SmartCardUserPhone() {

   override fun number(): String = "817263748512"

   override fun code(): String = "+1"
}