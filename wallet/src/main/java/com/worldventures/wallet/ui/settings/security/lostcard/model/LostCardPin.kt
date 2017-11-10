package com.worldventures.wallet.ui.settings.security.lostcard.model

import com.google.android.gms.maps.model.LatLng
import com.worldventures.wallet.domain.entity.lostcard.WalletAddress
import com.worldventures.wallet.domain.entity.lostcard.WalletPlace

data class LostCardPin(
   val places: List<WalletPlace>?,
   val address: WalletAddress?,
   val position: LatLng
)
