package com.worldventures.dreamtrips.social.feed.spek.translation

import com.messenger.api.TranslationInteractor
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.messenger.model.response.ImmutableTranslatedText
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.feed.model.TextualPost
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand
import com.worldventures.dreamtrips.modules.feed.service.storage.TranslationDiscStorage
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import rx.observers.TestSubscriber

class TranslationFeedInteractorSpec : BaseSpec({
   describe("Translate posts and comments actions") {
      setup({ setOf(TranslationDiscStorage(mockDb)) }) { mockHttpService() }

      context("Translate comment when disc storage is empty") {
         whenever(mockDb.getTranslation(anyString(), anyString())).thenReturn("")

         comment.message = "originalText"

         val testSubscribe = translateComment(comment, languageTo)

         assertActionSuccess(testSubscribe) {
            it.result.isTranslated && it.result.translation.equals(translationFromNetwork)
         }
      }

      context("Translate comment when disc storage is not empty") {
         whenever(mockDb.getTranslation(anyString(), anyString())).thenReturn(translationFromDisc)

         comment.message = "originalText"

         val testSubscribe = translateComment(comment, languageTo)

         assertActionSuccess(testSubscribe) {
            it.result.isTranslated && it.result.translation.equals(translationFromDisc)
         }
      }

      context("Translate post when disc storage is empty") {
         whenever(mockDb.getTranslation(anyString(), anyString())).thenReturn("")

         textualPost.description = "originalText"

         val testSubscribe = translatePost(textualPost, languageTo)

         assertActionSuccess(testSubscribe) {
            it.result.isTranslated && it.result.translation.equals(translationFromNetwork)
         }
      }

      context("Translate post when disc storage is not empty") {
         whenever(mockDb.getTranslation(anyString(), anyString())).thenReturn(translationFromDisc)

         textualPost.description = "originalText"

         val testSubscribe = translatePost(textualPost, languageTo)

         assertActionSuccess(testSubscribe) {
            it.result.isTranslated && it.result.translation.equals(translationFromDisc)
         }
      }
   }
}) {
   companion object BaseCompanion {
      val mockDb: SnappyRepository = spy()

      val languageTo = "en-US"
      val translationFromNetwork = "translationFromNetwork"
      val translationFromDisc = "translationFromDisc"
      val comment = Comment()
      val textualPost = TextualPost()

      lateinit var translateFeedInteractor: TranslationFeedInteractor
      lateinit var translationInteractor: TranslationInteractor

      fun setup(storageSet: () -> Set<ActionStorage<*>>, httpService: () -> MockHttpActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(storageSet())
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService().wrapStub().wrapCache())
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(SnappyRepository::class.java) { mockDb }
         daggerCommandActionService.registerProvider(TranslationFeedInteractor::class.java) { translateFeedInteractor }
         daggerCommandActionService.registerProvider(TranslationInteractor::class.java) { translationInteractor }

         translateFeedInteractor = TranslationFeedInteractor(SessionActionPipeCreator(janet))
         translationInteractor = TranslationInteractor(janet)
      }

      fun translateComment(comment: Comment, languageTo: String): TestSubscriber<ActionState<TranslateUidItemCommand.TranslateCommentCommand>> {
         val testSubscriber = TestSubscriber<ActionState<TranslateUidItemCommand.TranslateCommentCommand>>()

         translateFeedInteractor.translateCommentPipe().
               createObservable(TranslateUidItemCommand.TranslateCommentCommand(comment, languageTo))
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun translatePost(textualPost: TextualPost, languageTo: String): TestSubscriber<ActionState<TranslateUidItemCommand.TranslateFeedEntityCommand>> {
         val testSubscriber = TestSubscriber<ActionState<TranslateUidItemCommand.TranslateFeedEntityCommand>>()

         translateFeedInteractor.translateFeedEntityPipe().
               createObservable(TranslateUidItemCommand.TranslateFeedEntityCommand(textualPost, languageTo))
               .subscribe(testSubscriber)
         return testSubscriber
      }

      fun mockHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(ImmutableTranslatedText.builder().text(translationFromNetwork).build()))
               { request ->
                  request.url.contains("/api/translate")
               }
               .build()
      }
   }
}
