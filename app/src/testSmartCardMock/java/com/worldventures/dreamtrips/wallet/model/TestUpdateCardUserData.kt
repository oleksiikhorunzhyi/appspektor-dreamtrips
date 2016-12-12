package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData

class TestUpdateCardUserData : UpdateCardUserData {

   override fun middleName() = "middleName"

   override fun nameToDisplay() = "nameToDisplay"

   override fun photoUrl() = "http://img.com/image_123"

   override fun firstName() = "firstName"

   override fun lastName() = "lastName"
}