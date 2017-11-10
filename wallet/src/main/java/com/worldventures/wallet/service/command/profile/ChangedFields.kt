package com.worldventures.wallet.service.command.profile

import com.worldventures.wallet.domain.entity.SmartCardUserPhone
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto

data class ChangedFields(
      val firstName: String,
      val middleName: String,
      val lastName: String,
      val photo: SmartCardUserPhoto? = null,
      val phone: SmartCardUserPhone? = null
)