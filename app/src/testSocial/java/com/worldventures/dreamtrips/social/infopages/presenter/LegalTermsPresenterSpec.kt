package com.worldventures.dreamtrips.social.infopages.presenter

import com.nhaarman.mockito_kotlin.spy
import com.worldventures.dreamtrips.common.Injector
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor
import com.worldventures.dreamtrips.modules.infopages.presenter.LegalTermsPresenter
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand

class LegalTermsPresenterSpec: DocumentListPresenterSpec<LegalTermsPresenterSpec.LegalTermsPresenterTestBody,
      LegalTermsPresenter>({ LegalTermsPresenterTestBody() }) {

   class LegalTermsPresenterTestBody: DocumentListPresenterSpec.TestBody<LegalTermsPresenter>() {

      override fun describeTest(): String = "Legal Terms Presenter"

      override fun createPresenter(): LegalTermsPresenter = spy(LegalTermsPresenter())

      override fun getExpectedDocumentType(): GetDocumentsCommand.DocumentType = GetDocumentsCommand.DocumentType.LEGAL

      override fun onInjectSetup(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         super.onInjectSetup(injector, pipeCreator)
         injector.registerProvider(OfflineErrorInteractor::class.java, { OfflineErrorInteractor(pipeCreator) })
      }
   }
}



