package com.worldventures.dreamtrips.wallet.model

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto

class TestSmartCardUser: SmartCardUser() {

   override fun firstName(): String = "USER FIRST NAME"

   override fun userPhoto(): SmartCardUserPhoto = TestSmartCardUsurPhoto()
}
