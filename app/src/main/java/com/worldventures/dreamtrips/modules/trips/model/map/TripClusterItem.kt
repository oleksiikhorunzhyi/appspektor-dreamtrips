package com.worldventures.dreamtrips.modules.trips.model.map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class TripClusterItem(val pin: Pin) : ClusterItem {

   override fun getPosition() = LatLng(pin.coordinates.lat, pin.coordinates.lng)
}
