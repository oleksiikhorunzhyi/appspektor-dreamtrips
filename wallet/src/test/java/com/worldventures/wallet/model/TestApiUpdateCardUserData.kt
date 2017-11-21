package com.worldventures.wallet.model

import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData

class TestApiUpdateCardUserData : UpdateCardUserData {

   override fun phone() = TestApiCardUserPhone()

   override fun middleName() = "getMiddleName"

   override fun nameToDisplay() = "nameToDisplay"

   override fun photoUrl() = "http://img.com/image_123"

   override fun firstName() = "getFirstName"

   override fun lastName() = "getLastName"
}