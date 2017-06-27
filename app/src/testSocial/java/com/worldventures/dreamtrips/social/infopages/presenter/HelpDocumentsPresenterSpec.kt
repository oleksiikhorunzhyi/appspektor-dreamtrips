package com.worldventures.dreamtrips.social.infopages.presenter

import com.nhaarman.mockito_kotlin.spy
import com.worldventures.dreamtrips.modules.infopages.presenter.HelpDocumentListPresenter
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand
import com.worldventures.dreamtrips.social.infopages.presenter.DocumentListPresenterSpec

class HelpDocumentsPresenterSpec: DocumentListPresenterSpec<HelpDocumentsPresenterSpec.HelpDocumentsPresenterSpecBody,
      HelpDocumentListPresenter>({ HelpDocumentsPresenterSpecBody() }) {

   class HelpDocumentsPresenterSpecBody: DocumentListPresenterSpec.TestBody<HelpDocumentListPresenter>() {

      override fun describeTest(): String = "Help Documents Presenter"

      override fun createPresenter(): HelpDocumentListPresenter = spy(HelpDocumentListPresenter())

      override fun getExpectedDocumentType(): GetDocumentsCommand.DocumentType = GetDocumentsCommand.DocumentType.HELP
   }
}



