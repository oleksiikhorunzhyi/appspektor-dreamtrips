package com.worldventures.wallet.service.command.wizard

import com.worldventures.core.utils.ProjectTextUtils.defaultIfEmpty
import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardInfo
import com.worldventures.wallet.domain.entity.ApiSmartCardUser
import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.entity.SmartCardDetails
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.SmartCardUserPhone
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import io.techery.janet.Janet
import io.techery.mappery.MapperyContext
import rx.Observable

@Suppress("UnsafeCallOnNullableType")
internal class ProcessSmartCardInfoDelegate(
      private val walletStorage: WalletStorage,
      private val janetWallet: Janet,
      private val mappery: MapperyContext) {

   fun processSmartCardInfo(smartCardInfo: SmartCardInfo): Observable<Pair<SmartCard, SmartCardUser>> {
      val smartCard = createSmartCard(smartCardInfo)

      val user = createSmartCardUser(smartCardInfo.user())
      val result = Pair(smartCard, user)
      return save(result).map { result }
   }

   private fun createSmartCardUser(apiUser: ApiSmartCardUser): SmartCardUser {
      val photoUrl = apiUser.displayPhoto()
      val apiPhone = apiUser.phone()

      return SmartCardUser(
            firstName = defaultIfEmpty(apiUser.firstName(), ""),
            middleName = defaultIfEmpty(apiUser.middleName(), ""),
            lastName = defaultIfEmpty(apiUser.lastName(), ""),
            phoneNumber = if (apiPhone != null) mappery.convert(apiPhone, SmartCardUserPhone::class.java) else null,
            userPhoto = if (photoUrl.isNullOrEmpty()) null else SmartCardUserPhoto(photoUrl!!))
   }

   private fun createSmartCard(smartCardInfo: SmartCardInfo): SmartCard {
      return SmartCard(
            smartCardId = smartCardInfo.scId().toString(),
            cardStatus = CardStatus.ACTIVE,
            details = SmartCardDetails(
                  deviceId = smartCardInfo.deviceId(),
                  serialNumber = smartCardInfo.serialNumber(),
                  bleAddress = smartCardInfo.bleAddress(),
                  revVersion = smartCardInfo.revVersion(),
                  wvOrderId = smartCardInfo.wvOrderId(),
                  nxtOrderId = smartCardInfo.nxtOrderId(),
                  orderDate = smartCardInfo.orderDate())
      )
   }

   private fun save(result: Pair<SmartCard, SmartCardUser>): Observable<Void> {
      walletStorage.saveSmartCardUser(result.second)
      //saving smart card
      return janetWallet.createPipe(ActiveSmartCardCommand::class.java)
            .createObservableResult(ActiveSmartCardCommand(result.first))
            .map { null }
   }

}
