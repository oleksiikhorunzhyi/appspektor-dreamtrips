package com.worldventures.wallet.util

import com.google.android.gms.maps.model.LatLng
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation

object WalletLocationsUtil {

   fun toLatLng(position: WalletCoordinates) = LatLng(position.lat, position.lng)

   fun getLatestLocation(smartCardLocations: List<WalletLocation>): WalletLocation? =
         if (!smartCardLocations.isEmpty()) applyQueryToLocationList(smartCardLocations) else null

   private fun applyQueryToLocationList(smartCardLocations: List<WalletLocation>): WalletLocation =
         smartCardLocations.distinct().sortedWith(Comparator { location1, location2 ->
            location1.createdAt.compareTo(location2.createdAt)
         })
               .last()
}
