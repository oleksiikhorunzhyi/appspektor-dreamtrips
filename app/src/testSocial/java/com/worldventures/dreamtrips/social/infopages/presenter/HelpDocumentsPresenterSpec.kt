package com.worldventures.dreamtrips.social.infopages.presenter

import com.nhaarman.mockito_kotlin.mock
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.ui.infopages.presenter.HelpDocumentListPresenter
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe

class HelpDocumentsPresenterSpec : DocumentListPresenterSpec(HelpDocumentsTestSuite()) {

   class HelpDocumentsTestSuite : DocumentListTestSuite<HelpDocumentsComponents>(HelpDocumentsComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         describe("Help Documents Presenter") {

            super.specs().invoke(this)
         }
      }
   }

   class HelpDocumentsComponents : DocumentListComponents<HelpDocumentListPresenter>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = HelpDocumentListPresenter()
         view = mock()

         injector.inject(presenter)
      }
   }
}



