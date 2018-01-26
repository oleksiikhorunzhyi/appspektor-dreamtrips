package com.worldventures.dreamtrips.social.ui.profile.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.User
import com.worldventures.core.modules.auth.service.AuthInteractor
import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.core.service.DownloadFileInteractor
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.common.command.NotificationCountChangedCommand
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.CompoundOperationsCommand
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.QueryCompoundOperationsCommand
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.UploadingPresenterDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetAccountTimelineCommand
import com.worldventures.dreamtrips.social.ui.feed.storage.command.AccountTimelineStorageCommand
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.AccountTimelineStorageDelegate
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor
import com.worldventures.dreamtrips.social.ui.profile.service.command.UploadAvatarCommand
import com.worldventures.dreamtrips.social.ui.profile.service.command.UploadBackgroundCommand
import com.worldventures.dreamtrips.util.SocialCropImageManager
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.Contract
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.Observable
import rx.schedulers.Schedulers
import java.util.ArrayList
import java.util.Date
import kotlin.test.assertEquals

class AccountPresenterSpec : ProfilePresenterSpec(AccountTestSuite()) {

   class AccountTestSuite : ProfileTestSuite<AccountTestComponents>(AccountTestComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Account Presenter") {

               super.specs().invoke(this)

               describe("Basic interactions") {

                  it("should subscribe to data sources on view taken") {
                     init()
                     linkPresenterAndView()

                     val command = AccountTimelineStorageCommand(null)
                     command.result = emptyList()
                     whenever(accountTimelineStorageDelegate.observeStorageCommand()).thenReturn(Observable.just(command))

                     val presenter = presenter
                     presenter.feedItems = ArrayList()
                     presenter.onViewTaken()

                     verify(presenter).subscribeNotificationsBadgeUpdates()
                     verify(presenter).subscribeToAvatarUpdates()
                     verify(presenter).subscribeToBackgroundUpdates()
                     verify(presenter).subscribeToStorage()
                     verify(presenter).subscribeLoadNextFeeds()
                     verify(presenter).subscribeLoadNextFeeds()
                     verify(presenter).subscribeRefreshFeeds()
                     verify(presenter).connectToCroppedImageStream()
                     verify(presenter).subscribeToBackgroundUploadingOperations()
                  }

                  it("should update badge count on notification count change") {
                     init(BaseContract.of(NotificationCountChangedCommand::class.java)
                           .result(NotificationCountChangedCommand.NotificationCounterResult(1, 1, 1)))
                     linkPresenterAndView()

                     presenter.subscribeNotificationsBadgeUpdates()
                     userNotificationInteractor.notificationCountChangedPipe().send(NotificationCountChangedCommand())

                     verify(view).updateBadgeCount(any())
                  }

                  it("should refresh feed items on avatar update success") {
                     val updatedUser = User(1)
                     val avatar = User.Avatar()
                     avatar.medium = "updated"
                     updatedUser.avatar = avatar
                     init(Contract.of(UploadAvatarCommand::class.java).result(USER))
                     linkPresenterAndView()

                     presenter.subscribeToAvatarUpdates()
                     profileInteractor.uploadAvatarPipe().send(UploadAvatarCommand(""))

                     verify(presenter).onAvatarUploadSuccess()
                  }

                  it("should handle error on avatar update error") {
                     init(Contract.of(UploadAvatarCommand::class.java).exception(RuntimeException()))
                     linkPresenterAndView()

                     presenter.subscribeToAvatarUpdates()
                     profileInteractor.uploadAvatarPipe().send(UploadAvatarCommand(""))

                     verify(presenter).handleError(any(), any())
                     verify(presenter).refreshFeedItems()
                  }

                  it("should call cover upload success when cover is uploaded") {
                     init(Contract.of(UploadBackgroundCommand::class.java).result(USER))
                     linkPresenterAndView()

                     presenter.subscribeToBackgroundUpdates()
                     profileInteractor.uploadBackgroundPipe().send(UploadBackgroundCommand(""))

                     verify(presenter).onCoverUploadSuccess()
                  }
               }

               describe("Working with data") {
                  describe("Working with feed storage") {
                     it("should refresh timeline when storage is refreshed") {
                        init()
                        linkPresenterAndView()

                        val command = AccountTimelineStorageCommand(null)
                        command.result = emptyList()
                        whenever(accountTimelineStorageDelegate.observeStorageCommand()).thenReturn(Observable.just(command))

                        presenter.subscribeToStorage()

                        verify(presenter).timeLineUpdated(any())
                     }
                  }

                  describe("Refresh feed") {
                     it("should call refresh feed succeed when getting feed and stop pagination") {
                        init(Contract.of(GetAccountTimelineCommand.Refresh::class.java).result(emptyList<Any>()))
                        linkPresenterAndView()

                        presenter.subscribeRefreshFeeds()
                        feedInteractor.refreshAccountTimelinePipe.send(GetAccountTimelineCommand.Refresh())

                        verify(presenter).refreshFeedSucceed(any())
                     }

                     it("should call refresh feed succeed when getting feed and do not stop pagination") {
                        init(Contract.of(GetAccountTimelineCommand.Refresh::class.java).result(listOf(TextualPost())))
                        linkPresenterAndView()

                        presenter.subscribeRefreshFeeds()
                        feedInteractor.refreshAccountTimelinePipe.send(GetAccountTimelineCommand.Refresh())

                        verify(presenter).refreshFeedSucceed(any())
                     }

                     it("should call refresh feed error and do not stop pagination") {
                        init(Contract.of(GetAccountTimelineCommand.Refresh::class.java).exception(RuntimeException()))
                        linkPresenterAndView()

                        presenter.subscribeRefreshFeeds()
                        feedInteractor.refreshAccountTimelinePipe.send(GetAccountTimelineCommand.Refresh())

                        verify(presenter).refreshFeedError(any(), any())
                     }
                  }

                  describe("Load next timeline page") {
                     it("should call refresh feed succeed when getting feed and stop pagination") {
                        init(Contract.of(GetAccountTimelineCommand.LoadNext::class.java).result(emptyList<Any>()))
                        linkPresenterAndView()

                        presenter.subscribeLoadNextFeeds()
                        feedInteractor.loadNextAccountTimelinePipe.send(GetAccountTimelineCommand.LoadNext(Date()))

                        verify(presenter).addFeedItems(any())
                     }

                     it("should call refresh feed succeed when getting feed and do not stop pagination") {
                        init(Contract.of(GetAccountTimelineCommand.LoadNext::class.java).result(listOf(TextualPost())))
                        linkPresenterAndView()

                        presenter.subscribeLoadNextFeeds()
                        feedInteractor.loadNextAccountTimelinePipe.send(GetAccountTimelineCommand.LoadNext(Date()))

                        verify(presenter).addFeedItems(any())
                     }

                     it("should call refresh feed error and do not stop pagination") {
                        init(Contract.of(GetAccountTimelineCommand.LoadNext::class.java).exception(RuntimeException()))
                        linkPresenterAndView()

                        presenter.subscribeLoadNextFeeds()
                        feedInteractor.loadNextAccountTimelinePipe.send(GetAccountTimelineCommand.LoadNext(Date()))

                        verify(presenter).loadMoreItemsError(any(), any())
                     }
                  }

                  describe("Working with uploading operations") {
                     it("should refresh feed with compound operations") {
                        val uploadingOperations = emptyList<Any>()
                        init(Contract.of(CompoundOperationsCommand::class.java).result(uploadingOperations))
                        linkPresenterAndView()

                        presenter.subscribeToBackgroundUploadingOperations()
                        compoundOperationsInteractor.compoundOperationsPipe().send(QueryCompoundOperationsCommand())

                        verify(presenter).refreshFeedItems()
                        assertEquals(presenter.postUploads, uploadingOperations)
                     }
                  }
               }
            }
         }
      }

      override fun verifyFeedItemsRefreshedInView() {
         verify(components.view).refreshFeedItems(any(), any(), anyOrNull())
      }

      override fun verifyFeedItemsNeverRefreshedInView() {
         verify(components.view, never()).refreshFeedItems(any(), any(), any())
      }
   }

   class AccountTestComponents : ProfileTestComponents<AccountPresenter, AccountPresenter.View>() {

      val accountTimelineStorageDelegate: AccountTimelineStorageDelegate = mock()

      lateinit var compoundOperationsInteractor: CompoundOperationsInteractor
      lateinit var profileInteractor: ProfileInteractor
      lateinit var userNotificationInteractor: UserNotificationInteractor

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(AccountPresenter())
         presenter.user = USER
         view = mock()

         val downloadFileInteractor = DownloadFileInteractor(pipeCreator)
         val authInteractor = AuthInteractor(pipeCreator)
         compoundOperationsInteractor = CompoundOperationsInteractor(pipeCreator, Schedulers.immediate())
         userNotificationInteractor = UserNotificationInteractor(pipeCreator)
         profileInteractor = ProfileInteractor(pipeCreator, sessionHolder)

         injector.apply {
            registerProvider(UserNotificationInteractor::class.java, { userNotificationInteractor })
            registerProvider(CompoundOperationsInteractor::class.java, { compoundOperationsInteractor })
            registerProvider(AccountTimelineStorageDelegate::class.java, { accountTimelineStorageDelegate })
            registerProvider(ProfileInteractor::class.java, { profileInteractor })
            registerProvider(MediaPickerEventDelegate::class.java, { MediaPickerEventDelegate() })
            registerProvider(SocialCropImageManager::class.java, { SocialCropImageManager() })
            registerProvider(AuthInteractor::class.java, { authInteractor })
            registerProvider(SnappyRepository::class.java, { mock() })
            registerProvider(UploadingPresenterDelegate::class.java, { mock() })
            registerProvider(CachedModelHelper::class.java, { mock() })
            registerProvider(DownloadFileInteractor::class.java, { downloadFileInteractor })

            inject(presenter)
         }
      }
   }
}