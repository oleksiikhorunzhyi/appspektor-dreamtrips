package com.worldventures.dreamtrips.modules.tripsimages.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.core.navigation.Route
import com.worldventures.dreamtrips.modules.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.modules.background_uploading.service.command.QueryCompoundOperationsCommand
import com.worldventures.dreamtrips.modules.common.command.OfflineErrorCommand
import com.worldventures.dreamtrips.modules.common.model.User
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder
import com.worldventures.dreamtrips.modules.feed.model.PhotoFeedItem
import com.worldventures.dreamtrips.modules.feed.model.TextualPost
import com.worldventures.dreamtrips.modules.feed.presenter.FeedPresenterSpek.Companion.provideCompoundOperation
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.FeedEntityHolderDelegate
import com.worldventures.dreamtrips.modules.feed.presenter.delegate.UploadingPresenterDelegate
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo
import com.worldventures.dreamtrips.modules.tripsimages.model.PhotoMediaEntity
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.modules.tripsimages.service.command.*
import com.worldventures.dreamtrips.modules.tripsimages.view.args.TripImagesArgs
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.util.*

class TripImagesPresenterSpec : PresenterBaseSpec({
   describe("Trip images presenter") {
      beforeEachTest { init() }

      describe("View taken") {
         it("Should subscribe to required pipes, init and refresh items") {
            presenter.onViewTaken()

            verify(presenter).initItems()
            verify(presenter).initCreateMediaFlow()
            verify(presenter).subscribeToTripImages()
            verify(presenter).subscribeToPhotoDeletedEvents()
            verify(presenter).subscribeToErrorUpdates()
            verify(presenter).refreshImages()
            verify(presenter).subscribeToNewItems()
            verify(feedEntityHolderDelegate).subscribeToUpdates(any(), any(), any())
         }
      }

      describe("Init items") {
         it("Should create new array list if current items is null") {
            presenter.currentItems = null
            presenter.initItems()

            assert(presenter.currentItems != null)
         }

         it("Should not create new array list if current items is not null") {
            presenter.currentItems = ArrayList()
            presenter.currentItems.add(PhotoMediaEntity())

            presenter.initItems()

            assert(presenter.currentItems.size > 0)

         }
      }

      describe("Create button flow") {
         it("Route is member images, Should not hide create button and should subscribe to compound operations") {
            presenter.tripImagesArgs = memberImagesTripImagesArgs()
            presenter.initCreateMediaFlow()

            verify(view, never()).hideCreateImageButton()
            verify(presenter).subscribeToBackgroundUploadingOperations()
         }

         it("Route is account images and user is current, Should not hide create button and should subscribe to compound operations") {
            presenter.tripImagesArgs = userTripImagesArgs(ACCOUNT_USER_ID)
            presenter.initCreateMediaFlow()

            verify(view, never()).hideCreateImageButton()
            verify(presenter).subscribeToBackgroundUploadingOperations()
         }

         it("Route is account images and user is different from current, Should hide create button and should not subscribe to compound operations") {
            presenter.tripImagesArgs = userTripImagesArgs(OTHER_USER_ID)
            presenter.initCreateMediaFlow()

            verify(presenter, never()).subscribeToBackgroundUploadingOperations()
            verify(view).hideCreateImageButton()
         }
      }

      describe("Subscription to new images") {
         it("Refresh member images") {
            presenter.tripImagesArgs = memberImagesTripImagesArgs()
            val testSubscriber = TestSubscriber<ActionState<BaseMediaCommand>>()
            tripImagesInteractor.baseTripImagesCommandActionPipe().observe().subscribe(testSubscriber)

            presenter.refreshImages()

            val actionState = testSubscriber.onNextEvents[0]
            assert(actionState.action is GetMemberMediaCommand)
            assert(actionState.action.isReload && !actionState.action.isLoadMore)
         }

         it("Refresh user images") {
            presenter.tripImagesArgs = userTripImagesArgs(ACCOUNT_USER_ID)
            val testSubscriber = TestSubscriber<ActionState<BaseMediaCommand>>()
            tripImagesInteractor.baseTripImagesCommandActionPipe().observe().subscribe(testSubscriber)

            presenter.refreshImages()

            val actionState = testSubscriber.onNextEvents[0]
            assert(actionState.action is GetUsersMediaCommand)
            assert(actionState.action.isReload && !actionState.action.isLoadMore)
         }

         it("Load more user images") {
            presenter.currentItems = ArrayList(stubMemberImagesResponse())
            presenter.tripImagesArgs = userTripImagesArgs(ACCOUNT_USER_ID)
            val testSubscriber = TestSubscriber<ActionState<BaseMediaCommand>>()
            tripImagesInteractor.baseTripImagesCommandActionPipe().observe().subscribe(testSubscriber)

            presenter.loadNext()

            val actionState = testSubscriber.onNextEvents[0]
            assert(actionState.action is GetUsersMediaCommand)
            assert(!actionState.action.isReload && actionState.action.isLoadMore)
         }

         it("Load more member images") {
            presenter.currentItems = ArrayList(stubMemberImagesResponse())
            presenter.tripImagesArgs = memberImagesTripImagesArgs()
            val testSubscriber = TestSubscriber<ActionState<BaseMediaCommand>>()
            tripImagesInteractor.baseTripImagesCommandActionPipe().observe().subscribe(testSubscriber)

            presenter.loadNext()

            val actionState = testSubscriber.onNextEvents[0]
            assert(actionState.action is GetMemberMediaCommand)
            assert(!actionState.action.isReload && actionState.action.isLoadMore)
         }

         it("Refresh trip images list. Should call presenter.itemsUpdated") {
            presenter.currentItems = ArrayList()
            presenter.tripImagesArgs = memberImagesTripImagesArgs()
            presenter.subscribeToTripImages()

            tripImagesInteractor.baseTripImagesCommandActionPipe()
                  .send(tripImagesCommandFactory.provideCommand(memberImagesTripImagesArgs()))

            verify(presenter).itemsUpdated(any())
         }

         it("Get member images command fails") {
            presenter.tripImagesArgs = userTripImagesArgs(ACCOUNT_USER_ID)
            presenter.subscribeToTripImages()

            tripImagesInteractor.baseTripImagesCommandActionPipe()
                  .send(tripImagesCommandFactory.provideCommand(userTripImagesArgs(ACCOUNT_USER_ID)))

            verify(presenter).handleError(any(), any())
            assert(presenter.loading == false)
            verify(view).finishLoading()
         }

         it("Items updated with reload command, last page is not reached") {
            presenter.currentItems = spy(ArrayList())
            val mediaCommand: BaseMediaCommand = mock()

            doReturn(false).whenever(mediaCommand).lastPageReached()
            doReturn(true).whenever(mediaCommand).isReload
            doReturn(stubMemberImagesResponse()).whenever(mediaCommand).items

            presenter.itemsUpdated(mediaCommand)

            verify(presenter.currentItems).clear()
            verify(presenter.currentItems).addAll(stubMemberImagesResponse())
            assert(presenter.loading == false)
            assert(presenter.lastPageReached == false)
            verify(view).finishLoading()
            verify(presenter).updateItemsInView()
         }

         it("Items updated with load more command, last page is not reached") {
            presenter.currentItems = spy(ArrayList())
            val mediaCommand: BaseMediaCommand = mock()

            doReturn(false).whenever(mediaCommand).lastPageReached()
            doReturn(false).whenever(mediaCommand).isReload
            doReturn(stubMemberImagesResponse()).whenever(mediaCommand).items

            presenter.itemsUpdated(mediaCommand)

            verify(presenter.currentItems, never()).clear()
            verify(presenter.currentItems).addAll(stubMemberImagesResponse())
            assert(presenter.loading == false)
            assert(presenter.lastPageReached == false)
            verify(view).finishLoading()
            verify(presenter).updateItemsInView()
         }

         it("Items updated with load more command, last page is reached") {
            presenter.currentItems = spy(ArrayList())
            val mediaCommand: BaseMediaCommand = mock()

            doReturn(true).whenever(mediaCommand).lastPageReached()
            doReturn(false).whenever(mediaCommand).isReload
            doReturn(stubMemberImagesResponse()).whenever(mediaCommand).items

            presenter.itemsUpdated(mediaCommand)

            verify(presenter.currentItems, never()).clear()
            verify(presenter.currentItems).addAll(stubMemberImagesResponse())
            assert(presenter.loading == false)
            assert(presenter.lastPageReached == true)
            verify(view).finishLoading()
            verify(presenter).updateItemsInView()
         }
      }

      describe("Item deletion") {
         it("Item should be removed from collection") {
            presenter.currentItems = ArrayList(stubMemberImagesResponse())
            presenter.subscribeToPhotoDeletedEvents()
            tripImagesInteractor.deletePhotoPipe().send(DeletePhotoCommand(stubPhoto))

            assert(!presenter.currentItems.contains(stubPhotoMediaEntity))
         }
      }

      describe("Reload items") {
         it("View should hide new images button and refresh images should be called") {
            presenter.reload()

            verify(view).hideNewImagesButton()
            verify(presenter).refreshImages()
         }
      }

      describe("Subscription to error updates") {
         it("Should report no connection") {
            presenter.subscribeToErrorUpdates()

            offlineErrorInteractor.offlineErrorCommandPipe().send(OfflineErrorCommand())

            verify(presenter).reportNoConnection()
         }
      }

      describe("Subscription to new items") {
         it("Should call presenter.onFeedItemAdded") {
            presenter.subscribeToNewItems()

            postsInteractor.postCreatedPipe().send(PostCreatedCommand(stubTextualPost))

            verify(presenter).onFeedItemAdded(stubTextualPost)
         }

         it("Should call presenter.updateItemsInView, view.scrollToTop and items should contain stubPhotoMediaEntity") {
            presenter.currentItems = ArrayList()
            presenter.onFeedItemAdded(stubTextualPost)

            verify(presenter).updateItemsInView()
            verify(view).scrollToTop()
            assert(presenter.currentItems.contains(stubPhotoMediaEntity))
         }
      }

      describe("Subscription to background uploading operations") {
         it("Should contain all postCompoundOperations and call updateItemsInView") {
            presenter.currentItems = ArrayList()
            presenter.subscribeToBackgroundUploadingOperations()

            compoundOperationsInteractor.compoundOperationsPipe().send(QueryCompoundOperationsCommand())

            verify(presenter).updateItemsInView()
            assert(presenter.compoundOperationModels.containsAll(postCompoundOperations))
         }
      }
   }

}) {
   companion object {
      val ACCOUNT_USER_ID = 1
      val OTHER_USER_ID = 2
      val UID = "Fasdfasdfasdf"
      val stubPhoto = Photo(UID)
      val stubPhotoMediaEntity = PhotoMediaEntity(stubPhoto)
      val stubTextualPost = stubTextualPost()
      val stubPostCompoundOperationModel = provideCompoundOperation()
      val postCompoundOperations = listOf(stubPostCompoundOperationModel)

      lateinit var presenter: TripImagesPresenter
      lateinit var view: TripImagesPresenter.View

      lateinit var tripImagesInteractor: TripImagesInteractor
      lateinit var postsInteractor: PostsInteractor
      lateinit var compoundOperationsInteractor: CompoundOperationsInteractor
      lateinit var appConfigurationInteractor: AppConfigurationInteractor
      lateinit var offlineErrorInteractor: OfflineErrorInteractor

      val uploadingPresenterDelegate: UploadingPresenterDelegate = mock()
      val tripImagesCommandFactory: TripImagesCommandFactory = TripImagesCommandFactory()
      val feedEntityHolderDelegate: FeedEntityHolderDelegate = mock()

      fun init(args: TripImagesArgs = memberImagesTripImagesArgs()) {
         presenter = spy(TripImagesPresenter(args))
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(Contract.of(GetMemberMediaCommand::class.java).result(stubMemberImagesResponse()))
            addContract(Contract.of(GetUsersMediaCommand::class.java).exception(IllegalStateException()))
            addContract(Contract.of(DeletePhotoCommand::class.java).result(Photo(UID)))
            addContract(Contract.of(OfflineErrorCommand::class.java).result(RuntimeException("No Internet connection")))
            addContract(Contract.of(QueryCompoundOperationsCommand::class.java).result(postCompoundOperations))
         }.build()

         doReturn(User(ACCOUNT_USER_ID)).whenever(presenter).account

         val janet = Janet.Builder().addService(service).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)
         tripImagesInteractor = TripImagesInteractor(sessionPipeCreator)
         compoundOperationsInteractor = CompoundOperationsInteractor(sessionPipeCreator, Schedulers.immediate())
         postsInteractor = PostsInteractor(sessionPipeCreator)
         appConfigurationInteractor = AppConfigurationInteractor(janet)
         offlineErrorInteractor = OfflineErrorInteractor(sessionPipeCreator)

         prepareInjector().apply {
            registerProvider(TripImagesInteractor::class.java, { tripImagesInteractor })
            registerProvider(PostsInteractor::class.java, { postsInteractor })
            registerProvider(CompoundOperationsInteractor::class.java, { compoundOperationsInteractor })
            registerProvider(AppConfigurationInteractor::class.java, { appConfigurationInteractor })
            registerProvider(UploadingPresenterDelegate::class.java, { uploadingPresenterDelegate })
            registerProvider(TripImagesCommandFactory::class.java, { tripImagesCommandFactory })
            registerProvider(FeedEntityHolderDelegate::class.java, { feedEntityHolderDelegate })
            registerProvider(OfflineErrorInteractor::class.java, { offlineErrorInteractor })

            inject(presenter)
         }

         presenter.takeView(view)
      }

      private fun memberImagesTripImagesArgs() = TripImagesArgs.builder().route(Route.MEMBERS_IMAGES).build()

      private fun userTripImagesArgs(userId: Int) = TripImagesArgs.builder().route(Route.ACCOUNT_IMAGES).userId(userId).build()

      private fun stubMemberImagesResponse(): List<PhotoMediaEntity> {
         return listOf(stubPhotoMediaEntity)
      }

      private fun stubTextualPost() : TextualPost {
         val textualPost = TextualPost()
         textualPost.uid = "fsdafasdf"

         val feedEntityHolder = PhotoFeedItem()
         feedEntityHolder.type = FeedEntityHolder.Type.PHOTO
         feedEntityHolder.item = stubPhoto

         textualPost.attachments = listOf(feedEntityHolder)
         return textualPost
      }
   }
}