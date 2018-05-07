package com.worldventures.dreamtrips.social.infopages.presenter

import com.nhaarman.mockito_kotlin.mock
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor
import com.worldventures.dreamtrips.social.ui.infopages.presenter.LegalTermsPresenter
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe

class LegalTermsPresenterSpec : DocumentListPresenterSpec(LegalTermsTestSuite()) {

   class LegalTermsTestSuite : DocumentListPresenterSpec.DocumentListTestSuite<LegalTermsComponents>(LegalTermsComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         describe("Legal Terms Presenter") {

            super.specs().invoke(this)
         }
      }
   }

   class LegalTermsComponents : DocumentListPresenterSpec.DocumentListComponents<LegalTermsPresenter>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         view = mock()
         presenter = LegalTermsPresenter()

         injector.registerProvider(OfflineErrorInteractor::class.java, { OfflineErrorInteractor(pipeCreator) })
         injector.inject(presenter)
      }
   }
}
