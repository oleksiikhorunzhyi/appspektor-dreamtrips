package com.worldventures.wallet.service.command.http

import com.worldventures.core.modules.legal.LegalInteractor
import com.worldventures.core.modules.legal.command.AcceptTermsCommand
import com.worldventures.dreamtrips.api.smart_card.status.model.SmartCardStatus
import com.worldventures.dreamtrips.api.terms_and_conditions.model.BaseDocumentBody
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.storage.WalletStorage
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject

@CommandAction
class AcceptSmartCardAgreementsCommand(val smartCardId: String, val smartCardStatus: SmartCardStatus) : Command<Void?>(), InjectableAction {

   @Inject lateinit var legalInteractor: LegalInteractor
   @Inject lateinit var walletStorage: WalletStorage

   override fun run(callback: CommandCallback<Void?>) {
      Observable
            .zip(acceptAffidavit(), acceptTac(), { _, _ -> null })
            .subscribe(callback::onSuccess, callback::onFail)
   }

   private fun acceptAffidavit(): Observable<AcceptTermsCommand> {
      return Observable
            .fromCallable { walletStorage.smartCardAffidavit }
            .flatMap { affidavit -> legalInteractor.acceptTermsPipe
                  .createObservableResult(AcceptTermsCommand(BaseDocumentBody.SC_BETA_AFFIDAVIT, affidavit.version, smartCardId)) }
   }

   private fun acceptTac(): Observable<AcceptTermsCommand> {
      return Observable
            .fromCallable { walletStorage.walletTermsAndConditions }
            .flatMap { tac -> legalInteractor.acceptTermsPipe
                  .createObservableResult(AcceptTermsCommand(BaseDocumentBody.SC_TERMS, tac.version, smartCardId)) }
   }
}
