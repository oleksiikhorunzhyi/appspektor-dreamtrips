package com.worldventures.wallet.service.command.http

import com.worldventures.dreamtrips.api.smart_card.firmware.GetFirmwareHttpAction
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareResponse
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.FirmwareUpdateData
import com.worldventures.wallet.domain.entity.SmartCardFirmware
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.service.FirmwareInteractor
import com.worldventures.wallet.service.firmware.FirmwareRepository
import com.worldventures.wallet.service.firmware.command.FirmwareInfoCachedCommand
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.util.SmartCardSDK
import javax.inject.Inject

@Suppress("UnsafeCallOnNullableType")
@CommandAction
class FetchFirmwareInfoCommand @JvmOverloads constructor(
      private val firmwareVersion: SmartCardFirmware,
      private val skipCache: Boolean = false,
      private val markAsStarted: Boolean = false) : Command<FirmwareUpdateData>(), InjectableAction {

   @Inject lateinit var janet: Janet
   @Inject lateinit var walletStorage: WalletStorage
   @Inject lateinit var firmwareRepository: FirmwareRepository
   @Inject lateinit var firmwareInteractor: FirmwareInteractor

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<FirmwareUpdateData>) {
      if (!skipCache && firmwareRepository.firmwareUpdateData != null && firmwareRepository.firmwareUpdateData.isStarted) {
         callback.onSuccess(firmwareRepository.firmwareUpdateData)
      } else {
         janet.createPipe(GetFirmwareHttpAction::class.java)
               .createObservableResult(GetFirmwareHttpAction(getFirmwareVersion(), SmartCardSDK.getSDKVersion()))
               .map { createUpdateData(it.response()) }
               .subscribe({ firmwareUpdateData ->
                  firmwareInteractor.firmwareInfoCachedPipe().send(FirmwareInfoCachedCommand.save(firmwareUpdateData))
                  callback.onSuccess(firmwareUpdateData)
               }, { callback.onFail(it) })
      }
   }

   private fun createUpdateData(firmwareResponse: FirmwareResponse): FirmwareUpdateData {
      val smartCard = walletStorage.smartCard
      return FirmwareUpdateData(
            smartCardId = smartCard!!.smartCardId,
            currentFirmwareVersion = firmwareVersion,
            firmwareInfo = firmwareResponse.firmwareInfo(), //todo: create converter and store data in domain model
            isUpdateAvailable = firmwareResponse.updateAvailable(),
            isFactoryResetRequired = firmwareResponse.factoryResetRequired(),
            isUpdateCritical = firmwareResponse.updateCritical(),
            isStarted = markAsStarted)
   }

   private fun getFirmwareVersion(): String? {
      // in very first time user doesn't have installed firmware version, because this information are received only a from server.
      // so there was a decision to use a version of nordicApp firmware instead for performing request to check if there is a
      // new available update for a smart card in the server.
      return firmwareVersion.firmwareBundleVersion ?: firmwareVersion.nordicAppVersion
   }
}
