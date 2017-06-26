package com.worldventures.dreamtrips.social.background_uploading.spec.video

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.techery.spares.session.SessionHolder
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.common.janet.service.MockAnalyticsService
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.session.UserSession
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator
import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoProcessBunchStatus
import com.worldventures.dreamtrips.modules.background_uploading.model.video.VideoProcessStatus
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.modules.background_uploading.service.FeedItemsVideoProcessingStatusInteractor
import com.worldventures.dreamtrips.modules.background_uploading.service.PingAssetStatusInteractor
import com.worldventures.dreamtrips.modules.background_uploading.service.command.CompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.DeleteCompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.QueryCompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.UpdateCompoundOperationCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.PerformUpdateVideoStatusCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.UpdateVideoProcessStatusCommand
import com.worldventures.dreamtrips.modules.feed.model.FeedItem
import com.worldventures.dreamtrips.modules.feed.model.TextualPost
import com.worldventures.dreamtrips.modules.feed.model.VideoFeedItem
import com.worldventures.dreamtrips.modules.feed.model.video.Video
import com.worldventures.dreamtrips.social.background_uploading.spec.createPostBodyWithUploadedVideo
import com.worldventures.dreamtrips.social.background_uploading.spec.createPostCompoundOperationModel
import io.techery.janet.ActionService
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import io.techery.janet.http.test.MockHttpActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

class PingAssetStatusIndicatorSpec : BaseSpec ({

   describe("PingAssetStatusIndicator") {
      it("should delete compound completed operation on success") {
         initJanet(getProcessingCompoundOperation(), VideoProcessStatus.STATUS_COMPLETED)

         val deleteOperationSubscriber = TestSubscriber<ActionState<CompoundOperationsCommand>>()
         compoundOperationsInteractor.compoundOperationsPipe()
               .observe()
               .filter { it.action is DeleteCompoundOperationsCommand }
               .subscribe(deleteOperationSubscriber)

         val updateVideoStatusSubscriber = TestSubscriber<ActionState<UpdateVideoProcessStatusCommand>>()
         assetStatusInteractor.updateVideoProcessStatusPipe().createObservable(UpdateVideoProcessStatusCommand())
               .subscribe(updateVideoStatusSubscriber)

         AssertUtil.assertActionSuccess(updateVideoStatusSubscriber) { true }
         AssertUtil.assertStatusCount(deleteOperationSubscriber, ActionState.Status.START, 1)
      }

      it("should refresh compound completed operation on fail") {
         initJanet(getProcessingCompoundOperation(), VideoProcessStatus.STATUS_ERROR)

         val updateOperationSubscriber = TestSubscriber<ActionState<CompoundOperationsCommand>>()
         compoundOperationsInteractor.compoundOperationsPipe()
               .observe()
               .filter { it.action is UpdateCompoundOperationCommand }
               .subscribe(updateOperationSubscriber)

         val updateVideoStatusSubscriber = TestSubscriber<ActionState<UpdateVideoProcessStatusCommand>>()
         assetStatusInteractor.updateVideoProcessStatusPipe().createObservable(UpdateVideoProcessStatusCommand())
               .subscribe(updateVideoStatusSubscriber)

         AssertUtil.assertActionSuccess(updateVideoStatusSubscriber) { true }
         AssertUtil.assertStatusCount(updateOperationSubscriber, ActionState.Status.START, 1)
      }

      it("should perform update video status command if there are some processing items") {
         initJanet(getProcessingCompoundOperation(), VideoProcessStatus.STATUS_COMPLETED)
         val updateOperationSubscriber = TestSubscriber<ActionState<UpdateVideoProcessStatusCommand>>()
         assetStatusInteractor.updateVideoProcessStatusPipe()
               .createObservable(UpdateVideoProcessStatusCommand())
               .subscribe(updateOperationSubscriber)

         val performUpdateVideoStatusCommand = PerformUpdateVideoStatusCommand()
         performUpdateVideoStatusCommand.setDelayToStartUpdateCommand(0)
         assetStatusInteractor.performUpdateVideoStatusPipe()
               .createObservable(performUpdateVideoStatusCommand)
               .subscribe()

         // testing update operation being started as result is enough for this command
         AssertUtil.assertStatusCount(updateOperationSubscriber, ActionState.Status.START, 1)
      }

      it("should remove processing operations if there are such items in the feed") {
         initJanet(getProcessingCompoundOperation(VIDEO_UID), VideoProcessStatus.STATUS_COMPLETED)
         val deleteOperationSubscriber = TestSubscriber<ActionState<CompoundOperationsCommand>>()
         compoundOperationsInteractor.compoundOperationsPipe()
               .observe()
               .filter { it.action is DeleteCompoundOperationsCommand }
               .subscribe(deleteOperationSubscriber)

         val feedItemsVideoProcessingSubscriber = makeFeedItemsWithVideo(VIDEO_UID)
         val feedItemsSubscriber = TestSubscriber<ActionState<FeedItemsVideoProcessingStatusCommand>>()
         videoProcessingStatusInteractor.videosProcessingPipe()
               .createObservable(FeedItemsVideoProcessingStatusCommand(feedItemsVideoProcessingSubscriber))
               .subscribe(feedItemsSubscriber)

         AssertUtil.assertActionSuccess(feedItemsSubscriber) { true }
         AssertUtil.assertStatusCount(deleteOperationSubscriber, ActionState.Status.START, 1)
      }
   }
}) {
   companion object {
      const val ASSET_ID = "Uxa1"
      const val VIDEO_UID = "1fab63"
      lateinit var assetStatusInteractor: PingAssetStatusInteractor
      lateinit var compoundOperationsInteractor: CompoundOperationsInteractor
      lateinit var videoProcessingStatusInteractor: FeedItemsVideoProcessingStatusInteractor

      fun initJanet(contracts: List<Contract>, videoProcessStatus: String) {
         val daggerCommandActionService = CommandActionService().wrapDagger()

         val janet = Janet.Builder()
               .addService(mockActionService(daggerCommandActionService, contracts))
               .addService(MockHttpActionService.Builder().apply {
                  bind(MockHttpActionService.Response(200).body(makeBunchVideoProcessingStatus(videoProcessStatus))) {
                     it.url.contains("api/v1/Asset/Status")
                  }
                  }.build())
               .addService(MockAnalyticsService())
               .build()

         daggerCommandActionService.registerProvider(Janet::class.java, { janet })
         daggerCommandActionService.registerProvider(CompoundOperationsInteractor::class.java, { compoundOperationsInteractor })
         daggerCommandActionService.registerProvider(PostCompoundOperationMutator::class.java, { PostCompoundOperationMutator(mockSessionHolder()) })

         val sessionPipeCreator = SessionActionPipeCreator(janet)

         assetStatusInteractor = PingAssetStatusInteractor(sessionPipeCreator)
         compoundOperationsInteractor = CompoundOperationsInteractor(sessionPipeCreator, Schedulers.immediate())
         videoProcessingStatusInteractor = FeedItemsVideoProcessingStatusInteractor(sessionPipeCreator)
      }

      fun mockActionService(service: ActionService, mockContracts: List<Contract>) = MockCommandActionService.Builder()
            .apply {
               actionService(service)
               for (contract in mockContracts) addContract(contract)
            }
            .build()

      fun makeBunchVideoProcessingStatus(videoProcessStatus: String): VideoProcessBunchStatus {
         val bunchStatus = mock<VideoProcessBunchStatus>()

         val status = mock<VideoProcessStatus>()
         whenever(status.assetStatus).thenReturn(videoProcessStatus)
         whenever(status.assetId).thenReturn(ASSET_ID)

         whenever(bunchStatus.videoProcessStatuses).thenReturn(listOf(status))
         return bunchStatus
      }

      fun mockSessionHolder(): SessionHolder<UserSession> {
         val sessionHolder: SessionHolder<UserSession> = mock()
         val userSession: UserSession = mock()
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         return sessionHolder
      }

      fun getProcessingCompoundOperation(videoUid:String? = null): List<Contract> {
         System.out.print("getProcessingCompoundOperationContract")
         return listOf(BaseContract.of(QueryCompoundOperationsCommand::class.java).result(
               listOf(createPostCompoundOperationModel(
                     createPostBodyWithUploadedVideo(ASSET_ID, videoUid),
                     CompoundOperationState.PROCESSING))))
      }

      fun makeFeedItemsWithVideo(uid: String):List<FeedItem<TextualPost>> {
         val item = FeedItem<TextualPost>()
         val post = TextualPost()
         item.item = post
         val feedEntityHolder = VideoFeedItem()
         val video = Video()
         video.uid = uid
         feedEntityHolder.item = video
         post.attachments = listOf(feedEntityHolder)
         return listOf(item)
      }
   }
}
