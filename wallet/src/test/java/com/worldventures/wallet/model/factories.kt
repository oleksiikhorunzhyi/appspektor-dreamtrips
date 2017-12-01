package com.worldventures.wallet.model

import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.FirmwareUpdateData
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.domain.entity.SmartCardFirmware
import com.worldventures.wallet.service.nxt.model.MultiResponseBody
import com.worldventures.wallet.service.nxt.model.MultiResponseElement
import com.worldventures.wallet.service.nxt.model.NxtSession
import com.worldventures.wallet.service.nxt.util.NxtBankCardHelper

fun createTestSmartCardFirmware() = SmartCardFirmware(
      firmwareBundleVersion = null,
      nordicAppVersion = "1.0.0-test",
      nrfBootloaderVersion = "1.0.0-test",
      internalAtmelVersion = "1.0.0-test",
      internalAtmelBootloaderVersion = "1.0.0-test",
      externalAtmelVersion = "1.0.0-test",
      externalAtmelBootloaderVersion = "1.0.0-test"
)

fun createTestNxtSession(nxtSessionToken: String) = NxtSession(nxtSessionToken)

fun createTestFirmwareUpdateData(scId: String, currentFirmwareVersion: SmartCardFirmware) = FirmwareUpdateData(
      smartCardId = scId,
      isUpdateAvailable = true,
      isStarted = false,
      isUpdateCritical = false,
      currentFirmwareVersion = currentFirmwareVersion,
      isFactoryResetRequired = false
)

fun createTestSmartCard(smartCardId: String, cardStatus: CardStatus = CardStatus.ACTIVE, deviceId: String = "smart_card_device_id") =
      SmartCard(smartCardId, cardStatus, deviceId)

fun createTestMultiResponseBody(recordIds: List<String?>, number: String, cvv: String,
                                track1: String, track2: String = track1, track3: String = track1): MultiResponseBody {
   val responseElements = mutableListOf<MultiResponseElement>()

   recordIds.reversed().forEach { refIdPrefix ->
      responseElements.addAll(
            listOf(
                  MultiResponseElement(value = number, referenceId = NxtBankCardHelper.prefixRefId(NxtBankCardHelper.PAN, refIdPrefix)),
                  MultiResponseElement(value = cvv, referenceId = NxtBankCardHelper.prefixRefId(NxtBankCardHelper.CVV, refIdPrefix)),
                  MultiResponseElement(value = track1, referenceId = NxtBankCardHelper.prefixRefId(NxtBankCardHelper.TRACK_1, refIdPrefix)),
                  MultiResponseElement(value = track2, referenceId = NxtBankCardHelper.prefixRefId(NxtBankCardHelper.TRACK_2, refIdPrefix)),
                  MultiResponseElement(value = track3, referenceId = NxtBankCardHelper.prefixRefId(NxtBankCardHelper.TRACK_3, refIdPrefix))
            )
      )
   }
   return MultiResponseBody(responseElements)
}