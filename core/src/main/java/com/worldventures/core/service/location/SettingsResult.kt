package com.worldventures.core.service.location

import com.google.android.gms.location.LocationSettingsResponse

data class SettingsResult(
      val response: LocationSettingsResponse? = null,
      val exception: Exception? = null)
