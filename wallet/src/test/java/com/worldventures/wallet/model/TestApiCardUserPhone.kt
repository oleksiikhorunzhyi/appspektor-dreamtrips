package com.worldventures.wallet.model

import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone

class TestApiCardUserPhone : CardUserPhone() {

   override fun code() = "+1"

   override fun number() = "111111111111"
}