package com.worldventures.wallet.analytics.locatecard

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.locatecard.action.BaseLocateSmartCardAction
import com.worldventures.wallet.service.lostcard.LostCardRepository
import com.worldventures.wallet.util.WalletLocationsUtil
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class LocateCardAnalyticsCommand(private val baseLocateSmartCardAction: BaseLocateSmartCardAction) : WalletAnalyticsCommand(baseLocateSmartCardAction) {

   @Inject lateinit var lostCardRepository: LostCardRepository

   @Throws(Throwable::class)
   override fun run(callback: CommandCallback<Void>) {
      attachLocation()
      super.run(callback)
   }

   private fun attachLocation() {
      val locations = lostCardRepository.walletLocations
      val lastKnownLocation = WalletLocationsUtil.getLatestLocation(locations)
      if (lastKnownLocation != null) {
         baseLocateSmartCardAction.setLocation(lastKnownLocation.coordinates)
      }
   }
}
