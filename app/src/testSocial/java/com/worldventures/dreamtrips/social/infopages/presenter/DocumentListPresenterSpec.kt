package com.worldventures.dreamtrips.social.infopages.presenter

import com.nhaarman.mockito_kotlin.spy
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
import org.jetbrains.spek.api.dsl.Spec
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

abstract class DocumentListPresenterSpec<T: DocumentListPresenterSpec.TestBody<P>,
      P: DocumentListPresenter>(testBody: () -> T): PresenterBaseSpec(testBody.invoke().getBody()) {

   abstract class TestBody<P: DocumentListPresenter> {

      lateinit var presenter: P
      lateinit var view: DocumentListPresenter.View

      abstract fun describeTest(): String
      abstract fun createPresenter(): P
      abstract fun getExpectedDocumentType(): GetDocumentsCommand.DocumentType
      open fun onInjectSetup(injector: Injector, pipeCreator: SessionActionPipeCreator){}

      fun getBody(): Spec.() -> Unit {
         return {
            describe(describeTest()) {

               context("success documents list response") {
                  val resultList = emptyList<Document>()
                  setup(Contract.of(GetDocumentsCommand::class.java).result(resultList))

                  it("should set items and hide progress on view") {

                     verify(view).hideProgress()
                     verify(view).setDocumentList(resultList)
                  }
               }

               context("error documents list response") {
                  setup(Contract.of(GetDocumentsCommand::class.java).exception(RuntimeException()))

                  it("should still set items and hide progress on view") {
                     verify(view).hideProgress()
                  }
               }
            }

         }
      }

      fun setup(contract: Contract) {
         presenter = createPresenter()

         val injector: Injector = prepareInjector()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(contract)
         }.build()
         val janet = Janet.Builder().addService(service)
         val pipeCreator = SessionActionPipeCreator(janet.build())
         injector.registerProvider(DocumentsInteractor::class.java, { DocumentsInteractor(pipeCreator) })

         onInjectSetup(injector, pipeCreator)
         injector.inject(presenter)

         view = spy()
         presenter.takeView(view)
      }
   }
}



