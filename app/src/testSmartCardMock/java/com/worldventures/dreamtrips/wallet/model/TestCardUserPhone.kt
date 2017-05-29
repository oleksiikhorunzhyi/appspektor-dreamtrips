package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone

class TestCardUserPhone : CardUserPhone() {

   override fun code() = "+1"

   override fun number() = "111111111111"
}