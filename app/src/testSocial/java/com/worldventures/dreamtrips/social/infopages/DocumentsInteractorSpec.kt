package com.worldventures.dreamtrips.social.infopages

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.documents.model.DocumentType
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle
import com.worldventures.dreamtrips.core.janet.cache.storage.KeyValuePaginatedDiskStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.KeyValuePaginatedMemoryStorage
import com.worldventures.dreamtrips.core.janet.cache.storage.KeyValueStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.infopages.model.Document
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand
import com.worldventures.dreamtrips.modules.infopages.service.storage.DocumentsStorage
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.ArgumentCaptor
import rx.observers.TestSubscriber
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class DocumentsInteractorSpec : BaseSpec({
   describe("Test get documents action") {

      context("Refresh documents") {

         context("Documents cache is empty") {
            val testSubscriber = TestSubscriber<ActionState<GetDocumentsCommand>>()

            whenever(documentsMemoryStorage.get(any())).thenReturn(null)
            whenever(documentsDiscStorage.get(any())).thenReturn(emptyList())

            documentsInteractor.documentsActionPipe
                  .createObservable(GetDocumentsCommand(DOCUMENT_TYPE_HELP, true))
                  .subscribe(testSubscriber)

            it("should contain new items") {
               AssertUtil.assertActionSuccess(testSubscriber) { it.items().containsAll(documents) }
            }
         }

         context("Documents cache is not empty") {
            val testSubscriber = TestSubscriber<ActionState<GetDocumentsCommand>>()

            whenever(documentsMemoryStorage.get(any())).thenReturn(storedDocuments)

            documentsInteractor.documentsActionPipe
                  .createObservable(GetDocumentsCommand(DOCUMENT_TYPE_HELP, true))
                  .subscribe(testSubscriber)

            it("Items should contain only new items") {
               AssertUtil.assertActionSuccess(testSubscriber) {
                  it.items().containsAll(documents) && !it.items().containsAll(storedDocuments)
               }
            }
         }
      }

      context("Load more documents") {
         val testSubscriber = TestSubscriber<ActionState<GetDocumentsCommand>>()

         whenever(documentsMemoryStorage.get(any())).thenReturn(storedDocuments)

         documentsInteractor.documentsActionPipe
               .createObservable(GetDocumentsCommand(DOCUMENT_TYPE_HELP))
               .subscribe(testSubscriber)

         it("Items should contain new items and stored items") {
            AssertUtil.assertActionSuccess(testSubscriber) {
               it.items().containsAll(documents) &&
                     it.items().containsAll(storedDocuments)
            }

         }
         }
      }

      context("Check key value logic") {
         val restoreBundleCaptor = ArgumentCaptor.forClass(CacheBundle::class.java)
         whenever(documentsMemoryStorage.get(restoreBundleCaptor.capture())).thenReturn(emptyList())
         val saveBundleCaptor = ArgumentCaptor.forClass(CacheBundle::class.java)
         whenever(documentsMemoryStorage.save(saveBundleCaptor.capture(), any())).then{}

         documentsInteractor.documentsActionPipe
               .createObservable(GetDocumentsCommand(DOCUMENT_TYPE_HELP))
               .subscribe(TestSubscriber<ActionState<GetDocumentsCommand>>())
         documentsInteractor.documentsActionPipe
               .createObservable(GetDocumentsCommand(DOCUMENT_TYPE_LEGAL))
               .subscribe(TestSubscriber<ActionState<GetDocumentsCommand>>())

         it("should save and restore documents using different keys") {
            val helpDocumentsRestoreKey = restoreBundleCaptor.allValues[0].get<String>(KeyValueStorage.BUNDLE_KEY_VALUE)
            val legalDocumentsRestoreKey = restoreBundleCaptor.allValues[1].get<String>(KeyValueStorage.BUNDLE_KEY_VALUE)
            assertNotEquals(helpDocumentsRestoreKey, legalDocumentsRestoreKey, "data was restored using the same key")

            val helpDocumentsSaveKey = saveBundleCaptor.allValues[0].get<String>(KeyValueStorage.BUNDLE_KEY_VALUE)
            val legalDocumentsSaveKey = saveBundleCaptor.allValues[1].get<String>(KeyValueStorage.BUNDLE_KEY_VALUE)
            assertNotEquals(helpDocumentsSaveKey, legalDocumentsSaveKey, "data was saved using the same key")

            assertEquals(helpDocumentsRestoreKey, helpDocumentsSaveKey)
            assertEquals(legalDocumentsRestoreKey, legalDocumentsSaveKey)
         }
      }
}) {
   companion object {

      val DOCUMENT_TYPE_HELP = GetDocumentsCommand.DocumentType.HELP
      val DOCUMENT_TYPE_LEGAL = GetDocumentsCommand.DocumentType.LEGAL

      val apiDocuments = emptyList<Document>()

      val documents = listOf(Document("Techery", "Techery", "https://techery.io"),
            Document("Google", "Google", "https://google.com"))
      val storedDocuments = listOf(Document("Amazon", "Amazon", "https://amazon.com"),
            Document("HBO", "HBO", "https://hbo.com"))

      var documentsMemoryStorage: KeyValuePaginatedMemoryStorage<Document> = mock()
      val documentsDiscStorage: KeyValuePaginatedDiskStorage<Document> = mock()
      val storage = DocumentsStorage(documentsMemoryStorage, documentsDiscStorage)

      val mappery: MapperyContext = mock()
      val snappyDb: SnappyRepository = mock()

      val documentsInteractor: DocumentsInteractor

      init {
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
         whenever(mappery.convert(DOCUMENT_TYPE_HELP, DocumentType::class.java))
               .thenReturn(DocumentType.GENERAL)
         whenever(mappery.convert(DOCUMENT_TYPE_LEGAL, DocumentType::class.java))
               .thenReturn(DocumentType.LEGAL)

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { mappery }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { snappyDb }

         documentsInteractor = DocumentsInteractor(SessionActionPipeCreator(janet))
      }
   }
}