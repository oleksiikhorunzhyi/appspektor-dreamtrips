package com.worldventures.wallet.domain.entity

import java.util.*

data class SmartCardUserPhone(val code: String, val number: String) {

   fun fullPhoneNumber(): String {
      return String.format(Locale.US, "%s%s", code, number)
   }
}
