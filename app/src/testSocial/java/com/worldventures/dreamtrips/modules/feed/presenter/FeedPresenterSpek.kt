package com.worldventures.dreamtrips.modules.feed.presenter

import com.messenger.util.UnreadConversationObservable
import com.nhaarman.mockito_kotlin.*
import com.techery.spares.utils.delegate.NotificationCountEventDelegate
import com.worldventures.dreamtrips.AssertUtil
import com.worldventures.dreamtrips.BaseSpec.Companion.anyString
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.core.session.CirclesInteractor
import com.worldventures.dreamtrips.modules.background_uploading.model.*
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.modules.background_uploading.service.PingAssetStatusInteractor
import com.worldventures.dreamtrips.modules.background_uploading.service.command.QueryCompoundOperationsCommand
import com.worldventures.dreamtrips.modules.background_uploading.service.command.video.FeedItemsVideoProcessingStatusCommand
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem
import com.worldventures.dreamtrips.modules.common.api.janet.command.GetCirclesCommand
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle
import com.worldventures.dreamtrips.modules.feed.model.FeedItem
import com.worldventures.dreamtrips.modules.feed.model.PostFeedItem
import com.worldventures.dreamtrips.modules.feed.model.TextualPost
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedActionHandlerDelegate
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor
import com.worldventures.dreamtrips.modules.feed.service.SuggestedPhotoInteractor
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedCommand
import com.worldventures.dreamtrips.modules.feed.service.command.SuggestedPhotoCommand
import com.worldventures.dreamtrips.modules.feed.storage.command.FeedStorageCommand
import com.worldventures.dreamtrips.modules.feed.storage.delegate.FeedStorageDelegate
import com.worldventures.dreamtrips.modules.feed.view.util.TranslationDelegate
import com.worldventures.dreamtrips.modules.friends.model.Circle
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel
import com.worldventures.dreamtrips.modules.trips.model.Location
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.mockito.ArgumentMatchers
import org.mockito.internal.verification.VerificationModeFactory
import rx.Observable
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.util.*

class FeedPresenterSpek : PresenterBaseSpec({
   describe("Feed Presenter") {
      beforeEachTest {
         init()
      }

      describe("Restore feed items") {
         it("Should create new empty feed items list if first creation") {
            presenter.restoreFeedItems(true)
            assert(presenter.feedItems != null && presenter.feedItems.size == 0)
         }

         it("Should persist feed items list if non-first creation") {
            presenter.restoreFeedItems(false)
            presenter.feedItems = ArrayList(listOf(PostFeedItem()))
            assert(presenter.feedItems != null && presenter.feedItems.size == 1)
         }
      }

      describe("Restore filter circle") {
         it("Should not be null if filter circle is not cached") {
            doReturn(null).whenever(snappy).filterCircle

            presenter.restoreCircle()
            assert(presenter.filterCircle != null)
         }

         it("Should be same as cached filter circle") {
            val cachedCircle = Circle()
            doReturn(cachedCircle).whenever(snappy).filterCircle
            presenter.restoreCircle()
            assert(presenter.filterCircle == cachedCircle)
         }
      }

      describe("Subscription to feed updates") {

         it("Should update feed items, call refresh feed, notify data set changed and send FeedItemsVideoProcessingStatusCommand") {
            val testSubscriber = TestSubscriber<ActionState<FeedItemsVideoProcessingStatusCommand>>()
            assetStatusInteractor.feedItemsVideoProcessingPipe().observe().subscribe(testSubscriber)
            presenter.feedItems = ArrayList()

            val command = FeedStorageCommand.dummyCommand()
            command.result = listOf(FeedItem.create(TextualPost()))
            doReturn(Observable.just(command)).whenever(feedStorageDelegate).observeStorageCommand()

            presenter.subscribeToStorage()

            assert(presenter.feedItems.containsAll(command.result))
            verify(view, VerificationModeFactory.times(1)).refreshFeedItems(command.result, null, null)
            verify(view, VerificationModeFactory.times(1)).dataSetChanged()
            AssertUtil.assertStatusCount(testSubscriber, ActionState.Status.START, 1)
         }

         it("Should notify user that error happened") {
            val command = FeedStorageCommand.dummyCommand()
            command.result = listOf(FeedItem.create(TextualPost()))
            doReturn(Observable.error<Any?>(IllegalStateException())).whenever(feedStorageDelegate).observeStorageCommand()

            presenter.subscribeToStorage()

            verify(view, VerificationModeFactory.times(1)).informUser(ArgumentMatchers.anyInt())
         }

      }

      describe("Circles") {
         it("Should hide blocking progress and show filters with circles from response") {
            presenter.actionFilter()

            verify(view, VerificationModeFactory.times(1)).hideBlockingProgress()
            verify(view, VerificationModeFactory.times(1)).showFilter(circles, null)
         }

         it("Should set filter circle, save this circle to snappy and send refresh feed command") {
            val testSubscriber = TestSubscriber<ActionState<GetAccountFeedCommand.Refresh>>()
            feedInteractor.refreshAccountFeedPipe.observe().subscribe(testSubscriber)

            presenter.applyFilter(circles[0])

            assert(presenter.filterCircle == circles[0])
            verify(snappy, VerificationModeFactory.times(1)).saveFilterCircle(presenter.filterCircle)
            testSubscriber.assertValueCount(1)
         }

         it("Should send GetCirclesCommand") {
            val testSubscriber = TestSubscriber<ActionState<GetCirclesCommand>>()
            circlesInteractor.pipe().observe().subscribe(testSubscriber)

            presenter.updateCircles()

            testSubscriber.assertValueCount(1)
         }
      }

      describe("Refresh feed") {
         it("Refresh feed succeeds, view should update loading status and finishLoading, SuggestPhotoCommand should be sent") {
            val testSubscriber = TestSubscriber<ActionState<SuggestedPhotoCommand>>()
            suggestedPhotoInteractor.suggestedPhotoCommandActionPipe.observe().subscribe(testSubscriber)

            presenter.subscribeRefreshFeeds()
            feedInteractor.refreshAccountFeedPipe.send(GetAccountFeedCommand.Refresh("circleId"))

            verify(view, VerificationModeFactory.times(1)).updateLoadingStatus(false)
            verify(view, VerificationModeFactory.times(1)).finishLoading()
            testSubscriber.assertValueCount(1)
         }
      }

      describe("Load more feed") {
         it("Load more feed succeeds, view should update loading status") {
            presenter.subscribeLoadNextFeeds()
            feedInteractor.loadNextAccountFeedPipe.send(GetAccountFeedCommand.LoadNext("circleId", Date()))

            verify(view, VerificationModeFactory.times(1)).updateLoadingStatus(false)
         }

         it("Load next should return false if feedItems collection is empty") {
            presenter.feedItems = ArrayList()
            presenter.filterCircle = Circle.withTitle("dummy")

            val loadNextStatus = presenter.loadNext()

            assert(loadNextStatus == false)
         }

         it("Load next should return true if feedItems collection is not empty and GetAccountFeedCommand.LoadNext should be sent") {
            presenter.feedItems = ArrayList(feedItems)
            presenter.filterCircle = Circle.withTitle("dummy")

            val testSubscriber = TestSubscriber<ActionState<GetAccountFeedCommand.LoadNext>>()
            feedInteractor.loadNextAccountFeedPipe.observe().subscribe(testSubscriber)

            val loadNextStatus = presenter.loadNext()

            assert(loadNextStatus == true)
            testSubscriber.assertValueCount(1)
         }

      }

      describe("Feed user interactions") {
         it("Should updateRequestCounts when menu inflated") {
            presenter.menuInflated()

            verify(view, VerificationModeFactory.times(1)).setRequestsCount(0)
            verify(view, VerificationModeFactory.times(1)).setUnreadConversationCount(0)
         }

         it("Should download image") {
            presenter.onDownloadImage("url")

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onDownloadImage(anyString(), any(), any())
         }

         it("Should call like item") {
            presenter.onLikeItem(PostFeedItem())

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onLikeItem(any())
         }

         it("Should open comments") {
            presenter.onCommentItem(PostFeedItem())

            verify(view, VerificationModeFactory.times(1)).openComments(any())
         }

         it("Should translate item") {
            presenter.onTranslateFeedEntity(TextualPost())

            verify(translationDelegate, VerificationModeFactory.times(1)).translate(any())
         }

         it("Should show original text") {
            presenter.onShowOriginal(TextualPost())

            verify(translationDelegate, VerificationModeFactory.times(1)).showOriginal(any())
         }

         it("Should load flags") {
            presenter.onLoadFlags(mock())

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onLoadFlags(any(), any())
         }

         it("Should flag item") {
            presenter.onFlagItem("uid", 0, "reason")

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onFlagItem(any(), any(), any())
         }

         it("Should edit textual post") {
            presenter.onEditTextualPost(TextualPost())

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onEditTextualPost(any())
         }

         it("Should delete textual post") {
            presenter.onDeleteTextualPost(TextualPost())

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onDeleteTextualPost(any())
         }

         it("Should edit photo") {
            presenter.onEditPhoto(Photo())

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onEditPhoto(any())
         }

         it("Should delete photo") {
            presenter.onDeletePhoto(Photo())

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onDeletePhoto(any())
         }

         it("Should edit bucket item") {
            presenter.onEditBucketItem(BucketItem(), BucketItem.BucketType.ACTIVITY)

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onEditBucketItem(any(), any())
         }

         it("Should delete bucket item") {
            presenter.onDeleteBucketItem(BucketItem())

            verify(feedActionHandlerDelegate, VerificationModeFactory.times(1)).onDeleteBucketItem(any())
         }
      }

      describe("Photo suggestions") {
         it("Should call takeView and subscribe to new photos notifications") {
            presenter.takeSuggestionView(mock(), mock(), mock())

            verify(suggestedPhotoCellHelper, VerificationModeFactory.times(1)).takeView(any(), any(), any())
            verify(suggestedPhotoCellHelper, VerificationModeFactory.times(1)).subscribeNewPhotoNotifications(any())
         }

         it("Suggested photos collection should contain all photos from the command and call refreshFeedItems") {
            doReturn(0L).whenever(snappy).lastSuggestedPhotosSyncTime
            presenter.subscribePhotoGalleryCheck()
            suggestedPhotoInteractor.suggestedPhotoCommandActionPipe.send(SuggestedPhotoCommand())

            assert(presenter.suggestedPhotos.containsAll(suggestedPhotos))
            verify(view, VerificationModeFactory.times(1)).refreshFeedItems(null, null, presenter.suggestedPhotos)
         }

         it("Suggested photos collection should be clear, suggestedPhotoCellHelper should call reset and call refreshFeedItems") {
            presenter.suggestedPhotos = suggestedPhotos
            presenter.removeSuggestedPhotos()

            assert(presenter.suggestedPhotos.isEmpty())
            verify(suggestedPhotoCellHelper, VerificationModeFactory.times(1)).reset()
            verify(view, VerificationModeFactory.times(1)).refreshFeedItems(null, null, presenter.suggestedPhotos)
         }

         it("Should call sync of suggestedPhotoCellHelper") {
            presenter.syncSuggestionViewState()
            verify(suggestedPhotoCellHelper, VerificationModeFactory.times(1)).sync()
         }

         it("Should call selectPhoto of suggestedPhotoCellHelper") {
            presenter.selectPhoto(PhotoPickerModel())

            verify(suggestedPhotoCellHelper, VerificationModeFactory.times(1)).selectPhoto(any())
         }

         it("Should post selected photos to mediaPickerEventDelegate") {
            val mediaAttachment = MediaAttachment(emptyList(), MediaAttachment.Source.GALLERY)
            doReturn(Observable.just(mediaAttachment)).whenever(suggestedPhotoCellHelper).mediaAttachmentObservable()
            presenter.attachSelectedSuggestionPhotos()

            verify(mediaPickerEventDelegate, VerificationModeFactory.times(1)).post(mediaAttachment)
         }
      }

      describe("Counters") {
         it("Should update unread converasation counter") {
            doReturn(Observable.just(1)).whenever(unreadConversationObservable).getObservable()
            presenter.subscribeUnreadConversationsCount()

            assert(presenter.unreadConversationCount == 1)
            verify(view, VerificationModeFactory.times(1)).setUnreadConversationCount(1)
         }

         it("Should update friend reauests counter") {
            doReturn(Observable.just(null)).whenever(notificationCountEventDelegate).getObservable()
            doReturn(1).whenever(snappy).getFriendsRequestsCount()

            presenter.subscribeFriendsNotificationsCount()

            verify(view, VerificationModeFactory.times(1)).setRequestsCount(1)
         }
      }

      describe("Uploading user actions") {
         it("Should call onUploadResume in uploadingPresenterDelegate") {
            presenter.onUploadResume(stubPostCompoundOperationModel)
            verify(uploadingPresenterDelegate, VerificationModeFactory.times(1)).onUploadResume(stubPostCompoundOperationModel)
         }

         it("Should call onUploadPaused in uploadingPresenterDelegate") {
            presenter.onUploadPaused(stubPostCompoundOperationModel)
            verify(uploadingPresenterDelegate, VerificationModeFactory.times(1)).onUploadPaused(stubPostCompoundOperationModel)
         }

         it("Should call onUploadRetry in uploadingPresenterDelegate") {
            presenter.onUploadRetry(stubPostCompoundOperationModel)
            verify(uploadingPresenterDelegate, VerificationModeFactory.times(1)).onUploadRetry(stubPostCompoundOperationModel)
         }

         it("Should call onUploadCancel in uploadingPresenterDelegate") {
            presenter.onUploadCancel(stubPostCompoundOperationModel)
            verify(uploadingPresenterDelegate, VerificationModeFactory.times(1)).onUploadCancel(stubPostCompoundOperationModel)
         }
      }

      describe("Compound operation") {
         it("Should contain all postCompoundOperations and call refresh feeds") {
            presenter.subscribeToBackgroundUploadingOperations()

            compoundOperationsInteractor.compoundOperationsPipe().send(QueryCompoundOperationsCommand())

            assert(presenter.postUploads.containsAll(postCompoundOperations))
            verify(view, VerificationModeFactory.times(1)).refreshFeedItems(null, presenter.postUploads, null)
         }
      }
   }
}) {
   companion object {
      lateinit var presenter: FeedPresenter
      lateinit var view: FeedPresenter.View
      lateinit var feedInteractor: FeedInteractor
      lateinit var compoundOperationsInteractor: CompoundOperationsInteractor
      lateinit var circlesInteractor: CirclesInteractor
      lateinit var assetStatusInteractor: PingAssetStatusInteractor
      lateinit var suggestedPhotoInteractor: SuggestedPhotoInteractor

      val feedActionHandlerDelegate: FeedActionHandlerDelegate = mock()
      val snappy: SnappyRepository = mock()
      val feedStorageDelegate: FeedStorageDelegate = mock()
      val translationDelegate: TranslationDelegate = mock()
      val suggestedPhotoCellHelper: SuggestedPhotoCellPresenterHelper = mock()
      val mediaPickerEventDelegate: MediaPickerEventDelegate = mock()
      val unreadConversationObservable: UnreadConversationObservable = mock()
      val notificationCountEventDelegate: NotificationCountEventDelegate = mock()
      val uploadingPresenterDelegate: UploadingPresenterDelegate = mock()

      val circles = provideCircles()
      val feedItems = provideFeedItems()
      val suggestedPhotos = provideSuggestedPhotos()
      val stubPostCompoundOperationModel = provideCompoundOperation()
      val postCompoundOperations = listOf(stubPostCompoundOperationModel)

      fun init() {
         presenter = FeedPresenter()
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(BaseContract.of(GetCirclesCommand::class.java).result(circles))
            addContract(BaseContract.of(QueryCompoundOperationsCommand::class.java).result(postCompoundOperations))
            addContract(BaseContract.of(GetAccountFeedCommand.Refresh::class.java).result(feedItems))
            addContract(BaseContract.of(GetAccountFeedCommand.LoadNext::class.java).result(feedItems))
            addContract(BaseContract.of(SuggestedPhotoCommand::class.java).result(suggestedPhotos))
         }.build()

         val janet = Janet.Builder().addService(service).build()
         feedInteractor = FeedInteractor(SessionActionPipeCreator(janet))
         compoundOperationsInteractor = CompoundOperationsInteractor(SessionActionPipeCreator(janet), Schedulers.immediate())
         assetStatusInteractor = PingAssetStatusInteractor(SessionActionPipeCreator(janet))
         suggestedPhotoInteractor = SuggestedPhotoInteractor(SessionActionPipeCreator(janet))
         circlesInteractor = CirclesInteractor(SessionActionPipeCreator(janet))

         prepareInjector().apply {
            registerProvider(SnappyRepository::class.java, { snappy })
            registerProvider(FeedStorageDelegate::class.java, { feedStorageDelegate })
            registerProvider(FeedInteractor::class.java, { feedInteractor })
            registerProvider(PingAssetStatusInteractor::class.java, { assetStatusInteractor })
            registerProvider(CirclesInteractor::class.java, { circlesInteractor })
            registerProvider(SuggestedPhotoInteractor::class.java, { suggestedPhotoInteractor })
            registerProvider(FeedActionHandlerDelegate::class.java, { feedActionHandlerDelegate })
            registerProvider(TranslationDelegate::class.java, { translationDelegate })
            registerProvider(SuggestedPhotoCellPresenterHelper::class.java, { suggestedPhotoCellHelper })
            registerProvider(MediaPickerEventDelegate::class.java, { mediaPickerEventDelegate })
            registerProvider(UnreadConversationObservable::class.java, { unreadConversationObservable })
            registerProvider(NotificationCountEventDelegate::class.java, { notificationCountEventDelegate })
            registerProvider(UploadingPresenterDelegate::class.java, { uploadingPresenterDelegate })
            registerProvider(CompoundOperationsInteractor::class.java, { compoundOperationsInteractor })

            inject(presenter)
         }

         presenter.takeView(view)
      }

      fun providePhotoPickerModel(): PhotoPickerModel {
         val model = PhotoPickerModel()
         model.absolutePath = "dfsaasdfasdf"
         model.dateTaken = 100L
         return model
      }

      fun provideCompoundOperation(): PostCompoundOperationModel<PostBody> {
         return ImmutablePostCompoundOperationModel.builder<PostBody>()
               .id(134)
               .creationDate(Date())
               .type(PostBody.Type.TEXT)
               .state(CompoundOperationState.SCHEDULED)
               .body(ImmutableTextPostBody.builder()
                     .text("text")
                     .origin(CreateEntityBundle.Origin.FEED)
                     .location(Location())
                     .build())
               .build()!!
      }

      fun provideCircles(): List<Circle> = mutableListOf(Circle.withTitle("Friends"), Circle.withTitle("Close friends"))

      fun provideFeedItems(): List<PostFeedItem> = mutableListOf(PostFeedItem(), PostFeedItem())

      fun provideSuggestedPhotos(): List<PhotoPickerModel> = mutableListOf(providePhotoPickerModel(), providePhotoPickerModel())
   }
}