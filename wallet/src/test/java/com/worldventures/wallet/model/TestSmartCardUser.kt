package com.worldventures.wallet.model

import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.SmartCardUserPhone
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto

class TestSmartCardUser : SmartCardUser() {
   override fun phoneNumber(): SmartCardUserPhone? = null

   override fun firstName(): String = "USER FIRST NAME"

   override fun userPhoto(): SmartCardUserPhoto? = null
}
