package com.worldventures.wallet.ui.wizard.termsandconditionals

import com.worldventures.wallet.service.command.http.FetchTermsAndConditionsCommand
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import io.techery.janet.operationsubscriber.view.ErrorView
import io.techery.janet.operationsubscriber.view.OperationView

interface WizardTermsScreen : WalletScreen, ErrorView<FetchTermsAndConditionsCommand> {

   val agreementMode: AgreementMode

   fun showTerms(url: String)

   fun termsOperationView(): OperationView<FetchTermsAndConditionsCommand>

}
