package com.worldventures.wallet.domain.entity

enum class CardStatus {
   ACTIVE, IN_PROVISIONING;

   val isActive: Boolean
      get() = this == ACTIVE
}
