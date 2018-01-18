package com.worldventures.wallet.ui.wizard.termsandconditionals.impl

import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.service.command.http.FetchSmartCardAgreementsCommand
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.wizard.termsandconditionals.AgreementMode
import com.worldventures.wallet.ui.wizard.termsandconditionals.WizardTermsScreen
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers

abstract class AgreementDelegate(private val wizardInteractor: WizardInteractor) {

   open fun trackScreen() {
//      nothing
   }

   fun loadAgreements(view: WizardTermsScreen) {
      wizardInteractor.fetchSmartCardAgreementsPipe()
            .createObservable(createAgreementAction())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.termsOperationView())
                  .onSuccess { command -> view.showTerms(command.result) }
                  .create())

   }

   private fun createAgreementAction(): FetchSmartCardAgreementsCommand {
      return when (provideAgreementDocumentType()) {
         AgreementMode.TAC -> FetchSmartCardAgreementsCommand.termsAndConditions()
         AgreementMode.AFFIDAVIT -> FetchSmartCardAgreementsCommand.affidavit()
      }
   }

   abstract fun agreementsAccepted(navigator: Navigator)

   abstract fun provideAgreementDocumentType(): AgreementMode
}
