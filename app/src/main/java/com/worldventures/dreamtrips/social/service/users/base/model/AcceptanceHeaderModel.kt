package com.worldventures.dreamtrips.social.service.users.base.model

class AcceptanceHeaderModel(val acceptedCount: Int = 0) {
   override fun equals(other: Any?) = (other as? AcceptanceHeaderModel)?.acceptedCount == acceptedCount

   override fun hashCode() = acceptedCount
}
