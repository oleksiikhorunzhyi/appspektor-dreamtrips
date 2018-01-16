package com.worldventures.wallet.ui.wizard.input.helper

import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.http.AcceptSmartCardAgreementsCommand
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand
import com.worldventures.wallet.ui.common.base.screen.RxLifecycleView

import io.techery.janet.operationsubscriber.view.OperationView

interface InputDelegateView : RxLifecycleView {

   fun provideOperationFetchCardStatus(): OperationView<GetSmartCardStatusCommand>

   fun provideOperationFetchSmartCardUser(): OperationView<SmartCardUserCommand>

   fun provideOperationAcceptAgreements(): OperationView<AcceptSmartCardAgreementsCommand>

   fun showErrorCardIsAssignedDialog()
}
