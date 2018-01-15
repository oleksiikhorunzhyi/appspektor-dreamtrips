package com.worldventures.wallet.service.command.http

import com.worldventures.core.modules.legal.LegalInteractor
import com.worldventures.core.modules.legal.command.GetDocumentByTypeCommand
import com.worldventures.dreamtrips.api.terms_and_conditions.model.BaseDocumentBody
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.domain.entity.SmartCardAgreement
import com.worldventures.wallet.domain.storage.WalletStorage
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import rx.Observable
import javax.inject.Inject


@CommandAction
class FetchSmartCardAgreementsCommand private constructor(
      private val func: () -> String,
      private val storeFunc: (WalletStorage, SmartCardAgreement) -> Observable<String>
) : Command<String>(), InjectableAction {

   @Inject lateinit var legalInteractor: LegalInteractor
   @Inject lateinit var walletStorage: WalletStorage

   override fun run(callback: CommandCallback<String>) {
      legalInteractor.getDocumentByTypePipe
            .createObservableResult(GetDocumentByTypeCommand(func.invoke()))
            .map { command -> SmartCardAgreement(command.result.url(), command.result.version()) }
            .flatMap { agreement -> storeFunc.invoke(walletStorage, agreement) }
            .subscribe(callback::onSuccess, callback::onFail)
   }

   companion object {

      fun termsAndConditions(): FetchSmartCardAgreementsCommand {
         return FetchSmartCardAgreementsCommand(
               { BaseDocumentBody.SC_TERMS },
               { storage, agreement -> storage.saveWalletTermsAndConditions(agreement)
                  return@FetchSmartCardAgreementsCommand Observable.just(agreement.url) }
         )
      }

      fun affidavit(): FetchSmartCardAgreementsCommand {
         return FetchSmartCardAgreementsCommand(
               { BaseDocumentBody.SC_BETA_AFFIDAVIT },
               { storage, agreement -> storage.saveSmartCardAffidavit(agreement)
                  return@FetchSmartCardAgreementsCommand Observable.just(agreement.url) }
         )
      }
   }

}
