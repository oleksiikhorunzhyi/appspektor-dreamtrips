package com.worldventures.wallet.service.command.http

import com.worldventures.dreamtrips.api.smart_card.user_association.AssociateCardUserHttpAction
import com.worldventures.dreamtrips.api.smart_card.user_association.model.ImmutableAssociationCardUserData
import com.worldventures.dreamtrips.api.smart_card.user_association.model.ImmutableAssociationUserData
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.ApiSmartCardDetails
import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.entity.SmartCardDetails
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SystemPropertiesProvider
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import com.worldventures.wallet.util.WalletValidateHelper
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import javax.inject.Inject

private const val STUB_TAC_VERSION = 1

@CommandAction
class AssociateCardUserCommand(private val barcode: String,
                               private val updateCardUserData: UpdateCardUserData)
   : Command<SmartCard>(), InjectableAction {

   @Inject internal lateinit var janet: Janet
   @Inject internal lateinit var storage: WalletStorage
   @Inject internal lateinit var mapperyContext: MapperyContext
   @Inject internal lateinit var smartCardInteractor: SmartCardInteractor
   @Inject internal lateinit var propertiesProvider: SystemPropertiesProvider

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<SmartCard>) {
      WalletValidateHelper.validateSCIdOrThrow(barcode)

      val deviceId = propertiesProvider.deviceId()
      val userData = ImmutableAssociationUserData.builder()
            .firstName(updateCardUserData.firstName())
            .middleName(updateCardUserData.middleName())
            .lastName(updateCardUserData.lastName())
            .displayPhoto(updateCardUserData.photoUrl())
            .phone(updateCardUserData.phone())
            .build()

      val data = ImmutableAssociationCardUserData.builder()
            .scid(java.lang.Long.parseLong(barcode))
            .deviceModel(propertiesProvider.deviceName())
            .deviceOsVersion(propertiesProvider.osVersion())
            .deviceId(deviceId)
            .acceptedTermsAndConditionVersion(STUB_TAC_VERSION)
            .user(userData)
            .build()

      janet.createPipe(AssociateCardUserHttpAction::class.java)
            .createObservableResult(AssociateCardUserHttpAction(data))
            .map { createSmartCard(it.response(), deviceId) }
            .flatMap { smartCardInteractor.activeSmartCardPipe().createObservableResult(ActiveSmartCardCommand(it)) }
            .subscribe({ callback.onSuccess(it.result) }, { callback.onFail(it) })
   }

   private fun createSmartCard(source: ApiSmartCardDetails, deviceId: String): SmartCard {
      return SmartCard(
            smartCardId = source.scID().toString(),
            cardStatus = CardStatus.ACTIVE,
            details = SmartCardDetails(deviceId = deviceId))
   }
}
