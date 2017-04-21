package com.worldventures.dreamtrips.social.infopages

import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.dreamtrips.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.feedback.model.FeedbackAttachment
import com.worldventures.dreamtrips.api.feedback.model.FeedbackReason
import com.worldventures.dreamtrips.api.feedback.model.ImmutableFeedbackReason
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackType
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackTypeConverter
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor
import com.worldventures.dreamtrips.modules.infopages.service.command.GetFeedbackCommand
import com.worldventures.dreamtrips.modules.infopages.service.storage.FeedbackTypeStorage
import com.worldventures.dreamtrips.modules.mapping.converter.Converter
import com.worldventures.dreamtrips.modules.mapping.converter.FeedbackImageAttachmentConverter
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.junit.Assert
import org.mockito.internal.verification.VerificationModeFactory
import rx.observers.TestSubscriber
import java.util.*
import kotlin.test.assertEquals

class FeedbackInteractorSpec : BaseSpec({

   describe("Get feedback command") {

      setup(makeGetFeedbackReasonsHttpService())

      context("Should get correct feedback reasons") {
         val testSub = TestSubscriber<ActionState<GetFeedbackCommand>>()
         feedbackInteractor.feedbackPipe.createObservable(GetFeedbackCommand()).subscribe(testSub)
         assertActionSuccess(testSub) { checkIfFeedbackReasonsAreValid(it.result) }
      }

      context("Should restore feedback reasons from DB") {
         verify(mockDb, VerificationModeFactory.calls(2))
      }
   }

   describe("Image attachments mapper") {

      context("Should map entities correctly") {
         val stubInputAttachment = getStubImageAttachment()
         val mappedAttachment: FeedbackAttachment = getMappery().convert(stubInputAttachment, FeedbackAttachment::class.java)
         assertEquals(mappedAttachment.originUrl(), stubInputAttachment.url)
         assertEquals(mappedAttachment.type(), FeedbackAttachment.FeedbackType.IMAGE)
      }
   }

}) {
   companion object {
      val mockDb: SnappyRepository = spy()
      val stubFeedbackReasons: List<FeedbackReason> = makeStubFeedbackReasons()

      lateinit var feedbackInteractor: FeedbackInteractor

      fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .bindStorageSet(setOf(FeedbackTypeStorage(mockDb)))
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService.wrapStub().wrapCache())
               .build()

         daggerCommandActionService.registerProvider(MapperyContext::class.java) { getMappery() }
         daggerCommandActionService.registerProvider(Janet::class.java) { janet }

         feedbackInteractor = FeedbackInteractor(SessionActionPipeCreator(janet))
      }

      // getting feedback reasons specific

      fun makeGetFeedbackReasonsHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)
                     .body(stubFeedbackReasons)) { it.url.contains("/api/feedbacks/reason") }
               .build()
      }

      fun makeStubFeedbackReasons(): List<FeedbackReason> {
         val feedbackReasons = ArrayList<FeedbackReason>()
         for (i in 1..2) {
            val reason = ImmutableFeedbackReason.builder()
                  .id(i)
                  .text("Title $i")
                  .build()
            feedbackReasons.add(reason)
         }
         return feedbackReasons
      }

      fun checkIfFeedbackReasonsAreValid(responseFeedbacks: List<FeedbackType>): Boolean {
         Assert.assertTrue(stubFeedbackReasons.size == responseFeedbacks.size)
         for (i in 0..stubFeedbackReasons.size - 1) {
            val apiFeedbackReason = stubFeedbackReasons[i]
            val mappedFeedbackReason = responseFeedbacks[i]
            assertEquals(apiFeedbackReason.id(), mappedFeedbackReason.id)
            assertEquals(apiFeedbackReason.text(), mappedFeedbackReason.text)
         }
         return true
      }

      // mappers

      fun getMappery(): Mappery {
         val builder = Mappery.Builder()
         for (converter in constructConverters()) {
            builder.map(converter.sourceClass()).to(converter.targetClass(), converter)
         }
         return builder.build()
      }

      fun constructConverters(): List<Converter<Any, Any>> =
            listOf(castConverter(FeedbackTypeConverter()), castConverter(FeedbackImageAttachmentConverter()))

      // TODO Improve this, there should not be need for cast, but currently
      // it's not compilable if we return Converter<*, *>
      fun castConverter(converter: Converter<*, *>): Converter<Any, Any> {
         return converter as Converter<Any, Any>
      }

      fun getStubImageAttachment(): FeedbackImageAttachment {
         val random = Random().nextInt()
         val filePath = "somefilepath/somefile$random"
         val attachment = FeedbackImageAttachment(filePath)
         attachment.url = "http://somehost.com/$random.jpg"
         return attachment
      }
   }
}
