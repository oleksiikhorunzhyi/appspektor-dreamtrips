package com.worldventures.dreamtrips.social.infopages.service

import com.nhaarman.mockito_kotlin.mock
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.utils.AppVersionNameBuilder
import com.worldventures.core.service.DeviceInfoProvider
import com.worldventures.core.modules.infopages.service.FeedbackInteractor
import com.worldventures.core.modules.infopages.service.command.SendFeedbackCommand
import com.worldventures.dreamtrips.BuildConfig
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor
import com.worldventures.dreamtrips.wallet.service.TestSchedulerProvider
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import io.techery.janet.http.test.MockHttpActionService
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
      val smartCard = ImmutableSmartCard.builder().smartCardId("1")
            .cardStatus(SmartCard.CardStatus.ACTIVE).build()
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

         daggerCommandActionService.registerProvider(MapperyContext::class.java) { FeedbackInteractorSpec.getMappery() }
         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(AppVersionNameBuilder::class.java) {
            AppVersionNameBuilder(BuildConfig.versionMajor, BuildConfig.versionMinor,
               BuildConfig.versionPatch, BuildConfig.versionBuild,
               BuildConfig.FLAVOR, BuildConfig.BUILD_TYPE) }
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
   }
}