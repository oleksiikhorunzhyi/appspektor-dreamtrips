package com.worldventures.wallet.ui.records.detail

import com.worldventures.wallet.service.command.SetDefaultCardOnDeviceCommand
import com.worldventures.wallet.service.command.SetPaymentCardAction
import com.worldventures.wallet.service.command.record.DeleteRecordCommand
import com.worldventures.wallet.service.command.record.UpdateRecordCommand
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import io.techery.janet.operationsubscriber.view.OperationView

interface CardDetailsScreen : WalletScreen {

   val recordId: String

   val recordName: String

   var isSaveButtonEnabled: Boolean

   var defaultRecordDetails: DefaultRecordDetail?

   val isDataChanged: Boolean

   var cardNameErrorVisible: Boolean

   fun showDefaultCardDialog()

   fun showDeleteCardDialog()

   fun showNetworkConnectionErrorDialog()

   fun showCardIsReadyDialog(cardName: String)

   fun showSCNonConnectionDialog()

   fun notifyRecordDataIsSaved(newCardName: String)

   fun undoDefaultCardChanges()

   fun provideOperationSaveCardData(): OperationView<UpdateRecordCommand>

   fun provideOperationDeleteRecord(): OperationView<DeleteRecordCommand>

   fun provideOperationSetDefaultOnDevice(): OperationView<SetDefaultCardOnDeviceCommand>

   fun provideOperationSetPaymentCardAction(): OperationView<SetPaymentCardAction>
}
