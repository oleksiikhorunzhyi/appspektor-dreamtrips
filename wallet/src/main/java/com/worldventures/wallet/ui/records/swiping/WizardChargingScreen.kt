package com.worldventures.wallet.ui.records.swiping

import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.service.command.http.CreateRecordCommand
import com.worldventures.wallet.ui.common.base.screen.WalletScreen

import io.techery.janet.operationsubscriber.view.OperationView
import io.techery.janet.smartcard.action.charger.StartCardRecordingAction

interface WizardChargingScreen : WalletScreen {

   fun showSwipeError()

   fun trySwipeAgain()

   fun showSwipeSuccess()

   fun userPhoto(photo: SmartCardUserPhoto?)

   fun provideOperationCreateRecord(): OperationView<CreateRecordCommand>

   fun provideOperationStartCardRecording(): OperationView<StartCardRecordingAction>
}
