package com.worldventures.dreamtrips.social.infopages

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedMemoryStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.infopages.model.Document
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand
import com.worldventures.dreamtrips.modules.infopages.service.storage.DocumentsDiskStorage
import com.worldventures.dreamtrips.modules.infopages.service.storage.DocumentsStorage
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.MapperyContext
import rx.observers.TestSubscriber

class DocumentsInteractorSpec : BaseSpec({
   describe("Test get documents action") {
      setup()

      context("Refresh documents") {
         on("Documents cache is empty") {
            val testSubscriber = TestSubscriber<ActionState<GetDocumentsCommand>>()

            whenever(documentsMemoryStorage.get(any())).thenReturn(null)
            whenever(documentsDiscStorage.get(any())).thenReturn(emptyList())

            documentsInteractor.documentsActionPipe
                  .createObservable(GetDocumentsCommand(true))
                  .subscribe(testSubscriber)

            it("Items should contain new items") {
               AssertUtil.assertActionSuccess(testSubscriber) { it.items().containsAll(documents) }
            }
         }
         on("Documents cache is not empty") {
            val testSubscriber = TestSubscriber<ActionState<GetDocumentsCommand>>()

            whenever(documentsMemoryStorage.get(any())).thenReturn(storedDocuments)

            documentsInteractor.documentsActionPipe
                  .createObservable(GetDocumentsCommand(true))
                  .subscribe(testSubscriber)

            it("Items should contain only new items") {
               AssertUtil.assertActionSuccess(testSubscriber) {
                  it.items().containsAll(documents) && !it.items().containsAll(storedDocuments)
               }
            }
         }
      }

      context("Load more documents") {
         on("Documents cache is not empty") {
            val testSubscriber = TestSubscriber<ActionState<GetDocumentsCommand>>()

            whenever(documentsMemoryStorage.get(any())).thenReturn(storedDocuments)

            documentsInteractor.documentsActionPipe
                  .createObservable(GetDocumentsCommand())
                  .subscribe(testSubscriber)

            it("Items should contain new items and storedItems") {
               AssertUtil.assertActionSuccess(testSubscriber) {
                  it.items().containsAll(documents) &&
                        it.items().containsAll(storedDocuments)
               }
            }
         }
      }
   }
}) {
   companion object {

      val apiDocuments = emptyList<Document>()

      val documents = listOf(Document(1, "Techery", "https://techery.io"),
            Document(2, "Google", "https://google.com"))
      val storedDocuments = listOf(Document(3, "Amazon", "https://amazon.com"),
            Document(4, "HBO", "https://hbo.com"))

      val documentsMemoryStorage: PaginatedMemoryStorage<Document> = mock()
      val documentsDiscStorage: DocumentsDiskStorage = mock()
      val storage = DocumentsStorage(documentsMemoryStorage, documentsDiscStorage)

      val mappery: MapperyContext = mock()
      val snappyDb: SnappyRepository = mock()

      lateinit var documentsInteractor: DocumentsInteractor

      fun setup() {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOf(storage))
               .wrapDagger()

         val httpService = MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(apiDocuments)) { it.url.contains("/api/documents") }
               .build()

         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService)
               .build()

         whenever(mappery.convert(apiDocuments, Document::class.java))
               .thenReturn(documents)

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { snappyDb }

         documentsInteractor = DocumentsInteractor(SessionActionPipeCreator(janet))
      }
   }
}