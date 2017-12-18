package com.worldventures.wallet.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.converter.Converter
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.modules.infopages.model.converter.FeedbackImageAttachmentConverter
import com.worldventures.core.modules.infopages.model.converter.FeedbackTypeConverter
import com.worldventures.core.modules.infopages.service.FeedbackInteractor
import com.worldventures.core.modules.infopages.service.command.SendFeedbackCommand
import com.worldventures.core.service.DeviceInfoProvider
import com.worldventures.core.test.AssertUtil
import com.worldventures.core.utils.AppVersionNameBuilder
import com.worldventures.wallet.BaseSpec
import com.worldventures.wallet.domain.entity.CardStatus
import com.worldventures.wallet.domain.entity.SmartCard
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import io.techery.janet.http.test.MockHttpActionService
import io.techery.mappery.Mappery
import io.techery.mappery.MapperyContext
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class SendFeedbackInteractorSpec : BaseSpec({
   describe("Send feedback command") {
      setup(makeSendFeedbackHttpService())

      it("Should finish successfully") {
         val testSub = TestSubscriber<ActionState<SendFeedbackCommand>>()
         val command = SendFeedbackCommand(1, "text", emptyList())
         feedbackInteractor.sendFeedbackPipe().createObservable(command).subscribe(testSub)
         AssertUtil.assertActionSuccess(testSub) { true }
      }
   }
}) {
   companion object {
      val deviceInfoProvider: DeviceInfoProvider = mock()
      val smartCard = SmartCard(smartCardId = "1", cardStatus = CardStatus.ACTIVE, deviceId = "2303")
      val smartcardJanet = Janet.Builder()
            .addService(MockCommandActionService.Builder()
                  .actionService(CommandActionService())
                  .addContract(BaseContract.of(ActiveSmartCardCommand::class.java).result(smartCard))
                  .build())
            .build()
      val smartCardInteractor = SmartCardInteractor(SessionActionPipeCreator(smartcardJanet), TestSchedulerProvider())

      lateinit var feedbackInteractor: FeedbackInteractor

      fun setup(httpService: ActionService) {
         val daggerCommandActionService = CommandActionService()
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .addService(httpService.wrapStub().wrapCache())
               .build()
         val versionBuilder: AppVersionNameBuilder = mock()
         whenever(versionBuilder.semanticVersionName).thenReturn("semanticVersionName")
         whenever(deviceInfoProvider.isTablet).thenReturn(false)
         whenever(deviceInfoProvider.uniqueIdentifier).thenReturn("uniqueIdentifier")
         daggerCommandActionService.registerProvider(MapperyContext::class.java) { getMappery() }
         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(AppVersionNameBuilder::class.java) { versionBuilder }
         daggerCommandActionService.registerProvider(DeviceInfoProvider::class.java) { deviceInfoProvider }
         daggerCommandActionService.registerProvider(SmartCardInteractor::class.java) { smartCardInteractor }
         feedbackInteractor = FeedbackInteractor(SessionActionPipeCreator(janet))
      }

      fun makeSendFeedbackHttpService(): MockHttpActionService {
         return MockHttpActionService.Builder()
               .bind(MockHttpActionService.Response(200)) {
                  it.url.contains("/api/feedbacks")
               }
               .build()
      }

      fun getMappery(): Mappery {
         val builder = Mappery.Builder()
         for (converter in constructConverters()) {
            builder.map(converter.sourceClass()).to(converter.targetClass(), converter)
         }
         return builder.build()
      }

      fun constructConverters(): List<Converter<Any, Any>> =
            listOf(castConverter(FeedbackTypeConverter()), castConverter(FeedbackImageAttachmentConverter()))

      // TODO Resolve that
      // it's not compilable if we return Converter<*, *>
      fun castConverter(converter: Converter<*, *>): Converter<Any, Any> {
         return converter as Converter<Any, Any>
      }
   }
}
