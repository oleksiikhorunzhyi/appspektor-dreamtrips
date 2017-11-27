package com.worldventures.wallet.analytics.wizard

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.Attribute
import com.worldventures.core.service.analytics.AdobeTracker
import com.worldventures.wallet.analytics.WalletAnalyticsAction

@AnalyticsEvent(action = "wallet:setup:Step 5:Display Photo Has Been Set", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class PhotoWasSetAction private constructor(
      @field:Attribute("photomethod") internal val photoMethod: String, photoAdded: Boolean) : WalletAnalyticsAction() {

   @Attribute("displayphotoset") internal val displayPhotoSet: String? = if (photoAdded) "1" else null
   @Attribute("cardsetupstep5") internal val cardSetupStep5 = "1"

   companion object {

      private val PHOTO_METHOD_DEFAULT = "Default"
      private val PHOTO_METHOD_NO_PHOTO = "No Photo"

      fun methodDefault(): PhotoWasSetAction {
         return PhotoWasSetAction(PHOTO_METHOD_DEFAULT, true)
      }

      fun noPhoto(): PhotoWasSetAction {
         return PhotoWasSetAction(PHOTO_METHOD_NO_PHOTO, false)
      }
   }
}
