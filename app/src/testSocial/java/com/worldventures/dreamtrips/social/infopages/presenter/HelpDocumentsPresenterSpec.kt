package com.worldventures.dreamtrips.social.infopages.presenter

import com.nhaarman.mockito_kotlin.spy
import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand
import com.worldventures.dreamtrips.social.ui.infopages.presenter.HelpDocumentListPresenter

class HelpDocumentsPresenterSpec: DocumentListPresenterSpec<HelpDocumentsPresenterSpec.HelpDocumentsPresenterSpecBody,
      HelpDocumentListPresenter>({ HelpDocumentsPresenterSpecBody() }) {

   class HelpDocumentsPresenterSpecBody: DocumentListPresenterSpec.TestBody<HelpDocumentListPresenter>() {

      override fun describeTest(): String = "Help Documents Presenter"

      override fun createPresenter(): HelpDocumentListPresenter = spy(HelpDocumentListPresenter())

      override fun getExpectedDocumentType(): GetDocumentsCommand.DocumentType = GetDocumentsCommand.DocumentType.HELP
   }
}



