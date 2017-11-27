package com.worldventures.wallet.domain.entity

data class SmartCardUserPhone(val code: String, val number: String) {

   fun fullPhoneNumber(): String = "$code$number"
}
