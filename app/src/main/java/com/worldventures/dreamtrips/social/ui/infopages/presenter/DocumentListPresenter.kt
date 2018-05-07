package com.worldventures.dreamtrips.social.ui.infopages.presenter

import com.worldventures.core.modules.infopages.model.Document
import com.worldventures.core.modules.infopages.service.DocumentsInteractor
import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand
import com.worldventures.dreamtrips.modules.common.presenter.Presenter
import io.techery.janet.helper.ActionStateSubscriber
import javax.inject.Inject

abstract class DocumentListPresenter : Presenter<DocumentListPresenter.View>() {

   @Inject lateinit var documentsInteractor: DocumentsInteractor

   override fun takeView(view: View?) {
      super.takeView(view)
      observeDocumentsChanges()
      refreshDocuments()
   }

   private fun observeDocumentsChanges() {
      documentsInteractor.documentsActionPipe
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(ActionStateSubscriber<GetDocumentsCommand>()
                  .onStart(this::onDocumentsReloadingStarted)
                  .onSuccess(this::onDocumentsLoadSuccess)
                  .onFail(this::onDocumentsLoadError)
                  .onFinish { view.hideProgress() })
   }

   fun refreshDocuments() {
      documentsInteractor.documentsActionPipe.send(GetDocumentsCommand(getDocumentType(), true))
   }

   fun loadNextDocuments() {
      documentsInteractor.documentsActionPipe.send(GetDocumentsCommand(getDocumentType()))
   }

   private fun onDocumentsReloadingStarted(getDocumentsCommand: GetDocumentsCommand) {
      if (getDocumentsCommand.isRefresh) {
         view.showProgress()
         getDocumentsCommand.items().apply {
            if (view.isAdapterEmpty() && !isEmpty()) view.setDocumentList(this)
         }
      }
   }

   private fun onDocumentsLoadSuccess(getDocumentsCommand: GetDocumentsCommand) {
      view.updateLoadingStatus(getDocumentsCommand.isNoMoreElements())
      view.setDocumentList(getDocumentsCommand.items())
   }

   private fun onDocumentsLoadError(getDocumentsCommand: GetDocumentsCommand, throwable: Throwable) {
      view.setDocumentList(getDocumentsCommand.items())
      handleError(getDocumentsCommand, throwable)
   }

   fun getAnalyticsActionForOpenedItem(document: Document) = "${getAnalyticsSectionName()}:${document.originalName}"

   abstract fun track()

   protected abstract fun getAnalyticsSectionName(): String

   protected abstract fun getDocumentType(): GetDocumentsCommand.DocumentType

   interface View : Presenter.View {

      fun isAdapterEmpty(): Boolean

      fun setDocumentList(documentList: List<Document>)

      fun showProgress()

      fun hideProgress()

      fun updateLoadingStatus(noMoreElements: Boolean)
   }
}
