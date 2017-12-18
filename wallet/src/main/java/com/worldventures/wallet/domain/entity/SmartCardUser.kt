package com.worldventures.wallet.domain.entity

data class SmartCardUser(
      val firstName: String,
      val lastName: String = "",
      val middleName: String = "",
      val phoneNumber: SmartCardUserPhone? = null,
      val userPhoto: SmartCardUserPhoto? = null)
