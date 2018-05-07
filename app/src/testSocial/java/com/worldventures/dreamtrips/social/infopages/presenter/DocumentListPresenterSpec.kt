package com.worldventures.dreamtrips.social.infopages.presenter

import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.modules.infopages.model.Document
import com.worldventures.core.modules.infopages.service.DocumentsInteractor
import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.infopages.presenter.DocumentListPresenter
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.it

abstract class DocumentListPresenterSpec(suite: DocumentListTestSuite<DocumentListComponents<out DocumentListPresenter>>)
   : PresenterBaseSpec(suite) {

   abstract class DocumentListTestSuite<out C : DocumentListComponents<out DocumentListPresenter>>(components: C)
      : TestSuite<C>(components) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            context("success documents list response") {
               val resultList = emptyList<Document>()
               init(Contract.of(GetDocumentsCommand::class.java).result(resultList))
               linkPresenterAndView()

               it("should set items and hide progress on view") {

                  verify(view).hideProgress()
                  verify(view).setDocumentList(resultList)
               }
            }

            context("error documents list response") {
               init(Contract.of(GetDocumentsCommand::class.java).exception(RuntimeException()))
               linkPresenterAndView()

               it("should still set items and hide progress on view") {
                  verify(view).hideProgress()
               }
            }
         }
      }
   }

   abstract class DocumentListComponents<P : DocumentListPresenter> : TestComponents<P, DocumentListPresenter.View>() {

      fun init(contract: Contract) {
         val injector: Injector = prepareInjector()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(contract)
         }.build()
         val janet = Janet.Builder().addService(service)
         val pipeCreator = SessionActionPipeCreator(janet.build())
         injector.registerProvider(DocumentsInteractor::class.java, { DocumentsInteractor(pipeCreator) })

         onInit(injector, pipeCreator)
      }

      protected abstract fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator)
   }
}

