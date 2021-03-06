package com.worldventures.dreamtrips.social.ui.tripsimages.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.dreamtrips.modules.common.command.OfflineErrorCommand
import com.worldventures.dreamtrips.modules.common.service.OfflineErrorInteractor
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.QueryCompoundOperationsCommand
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoFeedItem
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedEntityHolderDelegate
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.UploadingPresenterDelegate
import com.worldventures.dreamtrips.social.ui.feed.presenter.provideCompoundOperation
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostCreatedCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.BaseMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetMemberMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetUsersMediaCommand
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.ImmutablePaginationParams
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.TripImagesCommandFactory
import com.worldventures.dreamtrips.social.ui.tripsimages.view.args.TripImagesArgs
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import java.util.ArrayList
import java.util.Date
import kotlin.test.assertFalse

class TripImagesPresenterSpec : PresenterBaseSpec(TripImagesTestSuite()) {

   class TripImagesTestSuite : TestSuite<TripImagesComponents>(TripImagesComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Trip images presenter") {
               beforeEachTest {
                  init()
                  linkPresenterAndView()
               }

               describe("Create button flow") {
                  it("Route is member images, Should not hide create button and should subscribe to compound operations") {
                     presenter.tripImagesArgs = memberImagesTripImagesArgs()
                     presenter.initCreateMediaFlow()

                     verify(view, never()).hideCreateImageButton()
                  }

                  it("Route is account images and user is current, Should not hide create button and should subscribe to compound operations") {
                     presenter.tripImagesArgs = userTripImagesArgs(ACCOUNT_USER_ID)
                     presenter.initCreateMediaFlow()

                     verify(view, never()).hideCreateImageButton()
                  }

                  it("Route is account images and user is different from current, Should hide create button and should not subscribe to compound operations") {
                     presenter.tripImagesArgs = userTripImagesArgs(OTHER_USER_ID)
                     presenter.initCreateMediaFlow()

                     verify(view).hideCreateImageButton()
                  }
               }

               describe("Subscription to new images") {
                  it("Refresh member images") {
                     presenter.tripImagesArgs = memberImagesTripImagesArgs()
                     val testSubscriber = TestSubscriber<ActionState<BaseMediaCommand>>()
                     tripImagesInteractor.baseTripImagesPipe.observe().subscribe(testSubscriber)

                     presenter.refreshImages()

                     val actionState = testSubscriber.onNextEvents[0]
                     assert(actionState.action is GetMemberMediaCommand)
                     assert(actionState.action.isReload && !actionState.action.isLoadMore)
                  }

                  it("Refresh user images") {
                     presenter.tripImagesArgs = userTripImagesArgs(ACCOUNT_USER_ID)
                     val testSubscriber = TestSubscriber<ActionState<BaseMediaCommand>>()
                     tripImagesInteractor.baseTripImagesPipe.observe().subscribe(testSubscriber)

                     presenter.refreshImages()

                     val actionState = testSubscriber.onNextEvents[0]
                     assert(actionState.action is GetUsersMediaCommand)
                     assert(actionState.action.isReload && !actionState.action.isLoadMore)
                  }

                  it("Load more user images") {
                     presenter.currentItems = ArrayList(stubMemberImagesResponse())
                     presenter.tripImagesArgs = userTripImagesArgs(ACCOUNT_USER_ID)
                     val testSubscriber = TestSubscriber<ActionState<BaseMediaCommand>>()
                     tripImagesInteractor.baseTripImagesPipe.observe().subscribe(testSubscriber)

                     presenter.loadNext()

                     val actionState = testSubscriber.onNextEvents[0]
                     assert(actionState.action is GetUsersMediaCommand)
                     assert(!actionState.action.isReload && actionState.action.isLoadMore)
                  }

                  it("Load more member images") {
                     presenter.currentItems = ArrayList(stubMemberImagesResponse())
                     presenter.tripImagesArgs = memberImagesTripImagesArgs()
                     val testSubscriber = TestSubscriber<ActionState<BaseMediaCommand>>()
                     tripImagesInteractor.baseTripImagesPipe.observe().subscribe(testSubscriber)

                     presenter.loadNext()

                     val actionState = testSubscriber.onNextEvents[0]
                     assert(actionState.action is GetMemberMediaCommand)
                     assert(!actionState.action.isReload && actionState.action.isLoadMore)
                  }

                  it("Get member images command fails") {
                     presenter.tripImagesArgs = userTripImagesArgs(ACCOUNT_USER_ID)
                     presenter.subscribeToTripImages()

                     tripImagesInteractor.baseTripImagesPipe
                           .send(tripImagesCommandFactory
                                 .provideCommand(userTripImagesArgs(ACCOUNT_USER_ID)))

                     assert(presenter.loading == false)
                     verify(view).finishLoading()
                  }

                  it("Items updated with reload command, last page is not reached") {
                     val args = memberImagesTripImagesArgs()
                     val stubMemberImagesResponse = stubMemberImagesResponse(PAGE_SIZE + 1)
                     init(args, Contract.of(GetUsersMediaCommand::class.java).result(stubMemberImagesResponse))
                     linkPresenterAndView()

                     val presenter = presenter
                     val view = view
                     presenter.currentItems = ArrayList()
                     presenter.subscribeToTripImages()
                     tripImagesInteractor.baseTripImagesPipe.send(GetUsersMediaCommand(args, paginationParams)
                           .apply { isReload = true })

                     assert(presenter.currentItems.isNotEmpty())
                     assert(presenter.loading == false)
                     assert(presenter.lastPageReached == false)
                     verify(view).finishLoading()
                     verify(view).updateItems(any(), any())
                  }

                  it("Items updated with load more command, last page is not reached") {
                     val args = memberImagesTripImagesArgs()
                     val stubMemberImagesResponse = stubMemberImagesResponse(PAGE_SIZE + 1)
                     init(args, Contract.of(GetUsersMediaCommand::class.java).result(stubMemberImagesResponse))
                     linkPresenterAndView()

                     val presenter = presenter
                     val view = view
                     presenter.currentItems = ArrayList()
                     presenter.subscribeToTripImages()

                     tripImagesInteractor.baseTripImagesPipe.send(GetUsersMediaCommand(args, paginationParams)
                           .apply { isLoadMore = true })

                     assert(presenter.currentItems.isNotEmpty())
                     assertFalse(presenter.loading)
                     assertFalse(presenter.lastPageReached)
                     verify(view).finishLoading()
                     verify(view).updateItems(any(), any())
                  }

                  it("Items updated with load more command, last page is reached") {
                     val args = memberImagesTripImagesArgs()
                     val stubMemberImagesResponse = stubMemberImagesResponse(PAGE_SIZE - 1)
                     init(args, Contract.of(GetUsersMediaCommand::class.java).result(stubMemberImagesResponse))
                     linkPresenterAndView()

                     val presenter = presenter
                     val view = view
                     presenter.currentItems = ArrayList()
                     presenter.subscribeToTripImages()
                     tripImagesInteractor.baseTripImagesPipe.send(GetUsersMediaCommand(args, paginationParams)
                           .apply { isReload = false; isLoadMore = true })

                     assert(presenter.currentItems.isNotEmpty())
                     assert(presenter.loading == false)
                     assert(presenter.lastPageReached == true)
                     verify(view).finishLoading()
                     verify(view).updateItems(any(), any())
                  }
               }

               describe("Item deletion") {
                  it("Item should be removed from collection") {
                     presenter.currentItems = ArrayList(stubMemberImagesResponse())
                     presenter.subscribeToPhotoDeletedEvents()
                     tripImagesInteractor.deletePhotoPipe.send(DeletePhotoCommand(stubPhoto))

                     assert(!presenter.currentItems.contains(stubPhotoMediaEntity))
                  }
               }

               describe("Reload items") {
                  it("View should hide new images button and refresh images should be called") {
                     val presenter = presenter
                     val view = view

                     presenter.currentItems = ArrayList()
                     presenter.subscribeToTripImages()
                     presenter.reload()

                     verify(view).hideNewImagesButton()
                     verify(view).updateItems(any(), any())
                  }
               }

               describe("Subscription to new items") {
                  it("Should refresh view when new items added") {
                     val presenter = presenter
                     val view = view

                     presenter.currentItems = ArrayList()
                     presenter.subscribeToNewItems()

                     postsInteractor.postCreatedPipe().send(PostCreatedCommand(stubTextualPost))

                     assert(presenter.currentItems.map { it.item }
                           .count { it == stubTextualPost.attachments[0].item } > 0)
                     verify(view).updateItems(any(), any())
                     verify(view).scrollToTop()
                  }
               }

               describe("Subscription to background uploading operations") {
                  it("Should contain all postCompoundOperations and call updateItemsInView") {
                     presenter.currentItems = ArrayList()
                     presenter.subscribeToBackgroundUploadingOperations()

                     compoundOperationsInteractor.compoundOperationsPipe().send(QueryCompoundOperationsCommand())

                     assert(presenter.compoundOperationModels.containsAll(postCompoundOperations))
                  }
               }
            }
         }
      }
   }

   class TripImagesComponents : TestComponents<TripImagesPresenter, TripImagesPresenter.View>() {

      val ACCOUNT_USER_ID = 1
      val OTHER_USER_ID = 2
      val PAGE_SIZE = 10
      private val UID = "Fasdfasdfasdf"
      val stubPhoto = Photo(UID)
      val stubPhotoMediaEntity = PhotoMediaEntity(stubPhoto)
      val stubTextualPost = stubTextualPost()
      private val stubPostCompoundOperationModel = provideCompoundOperation()
      val postCompoundOperations = listOf(stubPostCompoundOperationModel)
      val paginationParams: GetMemberMediaCommand.PaginationParams = ImmutablePaginationParams.builder().before(Date()).perPage((PAGE_SIZE)).build()

      val tripImagesCommandFactory: TripImagesCommandFactory = TripImagesCommandFactory()

      lateinit var postsInteractor: PostsInteractor
      lateinit var tripImagesInteractor: TripImagesInteractor
      lateinit var compoundOperationsInteractor: CompoundOperationsInteractor

      fun init(args: TripImagesArgs = memberImagesTripImagesArgs(), userImagesContract: Contract? = null) {
         presenter = TripImagesPresenter(args)
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(Contract.of(GetMemberMediaCommand::class.java).result(stubMemberImagesResponse()))
            val defaultUserImagesContract = Contract.of(GetUsersMediaCommand::class.java).exception(IllegalStateException())
            addContract(userImagesContract ?: defaultUserImagesContract)
            addContract(Contract.of(DeletePhotoCommand::class.java).result(Photo(UID)))
            addContract(Contract.of(OfflineErrorCommand::class.java).result(RuntimeException("No Internet connection")))
            addContract(Contract.of(QueryCompoundOperationsCommand::class.java).result(postCompoundOperations))
         }.build()

         val janet = Janet.Builder().addService(service).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)
         val appConfigurationInteractor = AppConfigurationInteractor(janet)
         val offlineErrorInteractor = OfflineErrorInteractor(sessionPipeCreator)
         postsInteractor = PostsInteractor(sessionPipeCreator)
         tripImagesInteractor = TripImagesInteractor(sessionPipeCreator)
         compoundOperationsInteractor = CompoundOperationsInteractor(sessionPipeCreator, Schedulers.immediate())

         prepareInjector(makeSessionHolder()).apply {
            registerProvider(TripImagesInteractor::class.java, { tripImagesInteractor })
            registerProvider(PostsInteractor::class.java, { postsInteractor })
            registerProvider(CompoundOperationsInteractor::class.java, { compoundOperationsInteractor })
            registerProvider(AppConfigurationInteractor::class.java, { appConfigurationInteractor })
            registerProvider(UploadingPresenterDelegate::class.java, { mock() })
            registerProvider(TripImagesCommandFactory::class.java, { tripImagesCommandFactory })
            registerProvider(FeedEntityHolderDelegate::class.java, { mock() })
            registerProvider(OfflineErrorInteractor::class.java, { offlineErrorInteractor })

            inject(presenter)
         }
      }

      fun memberImagesTripImagesArgs() = TripImagesArgs.builder()
            .pageSize(PAGE_SIZE)
            .type(TripImagesArgs.TripImageType.MEMBER_IMAGES)
            .build()

      fun userTripImagesArgs(userId: Int) = TripImagesArgs.builder().type(TripImagesArgs.TripImageType.ACCOUNT_IMAGES).userId(userId).build()

      fun stubMemberImagesResponse(size: Int = 1): List<PhotoMediaEntity> {
         val list = mutableListOf<PhotoMediaEntity>()
         for (i in 1..size) {
            list.add(stubPhotoMediaEntity)
         }
         return list
      }

      private fun makeSessionHolder(): SessionHolder {
         val user: User = mock()
         whenever(user.id).thenReturn(ACCOUNT_USER_ID)
         val userSession: UserSession = mock()
         whenever(userSession.user()).thenReturn(user)
         val mockSessionHolder: SessionHolder = mock()
         whenever(mockSessionHolder.get()).thenReturn(Optional.of(userSession))
         return mockSessionHolder
      }

      private fun stubTextualPost(): TextualPost {
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
