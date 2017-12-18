package com.worldventures.wallet.ui.dashboard.util.viewholder

import com.worldventures.core.ui.view.adapter.HeaderItem
import com.worldventures.wallet.domain.WalletConstants

data class CardStackHeaderHolder(
      val batteryLevel: Int = 0,
      val connected: Boolean = false,
      val lock: Boolean = true,
      val stealthMode: Boolean = false,
      val firstName: String = "",
      val middleName: String = "",
      val lastName: String = "",
      val photoUrl: String = "",
      val phoneNumber: String = "",
      val cardCount: Int = 0,
      val displayType: Int = WalletConstants.SMART_CARD_DEFAULT_DISPLAY_TYPE,
      private val title: String = ""
) : HeaderItem {
   override fun getHeaderTitle(): String? = title
}
