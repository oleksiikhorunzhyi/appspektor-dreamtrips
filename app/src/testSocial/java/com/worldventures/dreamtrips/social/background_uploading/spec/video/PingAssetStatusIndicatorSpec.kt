package com.worldventures.dreamtrips.social.background_uploading.spec.video

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.techery.spares.session.SessionHolder
import com.techery.spares.storage.complex_objects.Optional
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.api.post.model.response.ImmutablePostStatus
import com.worldventures.dreamtrips.api.post.model.response.PostStatus
import com.worldventures.dreamtrips.api.post.model.response.PostStatuses
import com.worldventures.dreamtrips.common.janet.service.MockAnalyticsService
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.session.UserSession
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor
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
         initJanet(getProcessingCompoundOperation(futurePostUid = FUTURE_POST_UID), getPostStatusesList( arrayOf(FUTURE_POST_UID)) )

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

      it("shouldn't refresh compound completed operation if post is not created on server") {
         initJanet(getProcessingCompoundOperation(futurePostUid = FUTURE_POST_UID), emptyArray())

         val updateOperationSubscriber = TestSubscriber<ActionState<CompoundOperationsCommand>>()
         compoundOperationsInteractor.compoundOperationsPipe()
               .observe()
               .filter { it.action is UpdateCompoundOperationCommand }
               .subscribe(updateOperationSubscriber)

         val updateVideoStatusSubscriber = TestSubscriber<ActionState<UpdateVideoProcessStatusCommand>>()
         assetStatusInteractor.updateVideoProcessStatusPipe().createObservable(UpdateVideoProcessStatusCommand())
               .subscribe(updateVideoStatusSubscriber)

         AssertUtil.assertActionSuccess(updateVideoStatusSubscriber) { true }
         AssertUtil.assertStatusCount(updateOperationSubscriber, ActionState.Status.START, 0)
      }

      it("should delete 1 compound completed operation from 2 if there only one post is created on server") {
         initJanet(getProcessingCompoundOperationList(), getPostStatusesList( arrayOf(FUTURE_POST_UID, FUTURE_POST_UID_2)) )
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
         initJanet(getProcessingCompoundOperation(VIDEO_UID, FUTURE_POST_UID),  getPostStatusesList( arrayOf(FUTURE_POST_UID)) )
         val deleteOperationSubscriber = TestSubscriber<ActionState<CompoundOperationsCommand>>()
         compoundOperationsInteractor.compoundOperationsPipe()
               .observe()
               .filter { it.action is DeleteCompoundOperationsCommand }
               .subscribe(deleteOperationSubscriber)

         val feedItemsVideoProcessingSubscriber = makeFeedItemsWithVideo(VIDEO_UID, FUTURE_POST_UID)
         val feedItemsSubscriber = TestSubscriber<ActionState<FeedItemsVideoProcessingStatusCommand>>()
         assetStatusInteractor.feedItemsVideoProcessingPipe()
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
      const val FUTURE_POST_UID = "9379992"

      const val ASSET_ID_2 = "2Uxa1"
      const val VIDEO_UID_2 = "21fab63"
      const val FUTURE_POST_UID_2 = "29379992"

      lateinit var assetStatusInteractor: PingAssetStatusInteractor
      lateinit var compoundOperationsInteractor: CompoundOperationsInteractor

      fun initJanet(contracts: List<Contract>, statuses: Array<PostStatus>) {
         val daggerCommandActionService = CommandActionService().wrapDagger()

         val janet = Janet.Builder()
               .addService(mockActionService(daggerCommandActionService, contracts))
               .addService(MockHttpActionService.Builder().apply {
                  bind(MockHttpActionService.Response(200).body(makeBunchVideoProcessingStatus(statuses))) {
                     it.url.contains("api/social/posts/exists")
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
      }

      fun mockActionService(service: ActionService, mockContracts: List<Contract>) = MockCommandActionService.Builder()
            .apply {
               actionService(service)
               for (contract in mockContracts) addContract(contract)
            }
            .build()

      fun makeBunchVideoProcessingStatus(statuses: Array<PostStatus>): PostStatuses {
         val postStatus = mock<PostStatuses>()
         whenever(postStatus.postStatuses()).thenReturn(statuses)
         return postStatus
      }

      fun mockSessionHolder(): SessionHolder {
         val sessionHolder: SessionHolder = mock()
         val userSession: UserSession = mock()
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         return sessionHolder
      }

      fun getProcessingCompoundOperation(videoUid:String? = null, futurePostUid: String? = null): List<Contract> {
         System.out.print("getProcessingCompoundOperationContract")
         val compoundOperationList = listOf(createPostCompoundOperationModel(
               createPostBodyWithUploadedVideo(ASSET_ID, videoUid, futurePostUid),
               CompoundOperationState.PROCESSING))
         return listOf(BaseContract.of(QueryCompoundOperationsCommand::class.java).result(compoundOperationList))
      }

      fun getProcessingCompoundOperationList(): List<Contract> {
         val compoundOperationList = listOf(
               createPostCompoundOperationModel(createPostBodyWithUploadedVideo(ASSET_ID, VIDEO_UID, FUTURE_POST_UID),
                     CompoundOperationState.PROCESSING),
               createPostCompoundOperationModel(createPostBodyWithUploadedVideo(ASSET_ID_2, VIDEO_UID_2, FUTURE_POST_UID_2),
                     CompoundOperationState.PROCESSING))
         return listOf(BaseContract.of(QueryCompoundOperationsCommand::class.java).result(compoundOperationList))
      }

      fun getPostStatusesList(ids: Array<String>) =
            Array<PostStatus>(ids.size, {
               ImmutablePostStatus.builder().uid(ids[it]).status(PostStatus.Status.COMPLETED).build()})

      fun makeFeedItemsWithVideo(videoUid: String, postUid: String):List<FeedItem<TextualPost>> {
         val item = FeedItem<TextualPost>()
         val post = TextualPost()
         post.uid = postUid
         item.item = post
         val feedEntityHolder = VideoFeedItem()
         val video = Video()
         video.uid = videoUid
         feedEntityHolder.item = video
         post.attachments = listOf(feedEntityHolder)
         return listOf(item)
      }
   }
}
