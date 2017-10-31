package com.worldventures.wallet.analytics.tokenization

import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.domain.entity.lostcard.WalletCoordinates
import com.worldventures.wallet.service.lostcard.LostCardRepository
import com.worldventures.wallet.util.WalletLocationsUtil
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class TokenizationAnalyticsLocationCommand(private val tokenizationCardAction: TokenizationCardAction) : WalletAnalyticsCommand(tokenizationCardAction) {

   @Inject internal lateinit var lostCardRepository: LostCardRepository

   @Throws(Throwable::class)
   override fun run(callback: CommandCallback<Void>) {
      tokenizationCardAction.setCoordinates(fetchLastKnownCoordinates())
      super.run(callback)
   }

   private fun fetchLastKnownCoordinates(): WalletCoordinates? {
      val locations = lostCardRepository.walletLocations
      val lastKnownLocation = WalletLocationsUtil.getLatestLocation(locations)
      return lastKnownLocation?.coordinates
   }

}