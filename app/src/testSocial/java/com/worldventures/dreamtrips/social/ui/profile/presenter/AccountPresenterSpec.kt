package com.worldventures.dreamtrips.social.ui.profile.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.User
import com.worldventures.core.modules.auth.service.AuthInteractor
import com.worldventures.core.modules.video.utils.CachedModelHelper
import com.worldventures.core.service.DownloadFileInteractor
import com.worldventures.dreamtrips.common.Injector
import com.worldventures.dreamtrips.core.repository.SnappyRepository
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor
import com.worldventures.dreamtrips.modules.common.command.NotificationCountChangedCommand
import com.worldventures.dreamtrips.social.ui.background_uploading.service.CompoundOperationsInteractor
import com.worldventures.dreamtrips.social.ui.background_uploading.service.PingAssetStatusInteractor
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
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.Contract
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.Observable
import rx.schedulers.Schedulers
import java.util.*
import kotlin.test.assertEquals

class AccountPresenterSpec : ProfilePresenterSpec(AccountTestBody()) {

   class AccountTestBody : ProfilePresenterSpec.TestBody<AccountPresenter, AccountPresenter.View>() {
      lateinit var downloadFileInteractor: DownloadFileInteractor
      lateinit var compoundOperationsInteractor: CompoundOperationsInteractor
      lateinit var pingAssetStatusInteractor: PingAssetStatusInteractor
      val mediaPickerEventDelegate = MediaPickerEventDelegate()
      val socialCropImageManager = SocialCropImageManager()
      lateinit var authInteractor: AuthInteractor
      lateinit var profileInteractor: ProfileInteractor
      lateinit var userNotificationInteractor: UserNotificationInteractor
      val snappyDb = mock<SnappyRepository>()
      val accountTimelineStorageDelegate: AccountTimelineStorageDelegate = mock()

      override fun getDescription(): String = "Account Presenter"

      override fun makePresenter() = AccountPresenter()

      override fun makeView(): AccountPresenter.View = mock()

      override fun makeExtendedSuite(): SpecBody.() -> Unit {
         return {
            describe("Basic interactions") {
               it("should subscribe to data sources on view taken") {
                  setup()

                  val command = AccountTimelineStorageCommand(null)
                  command.result = emptyList()
                  whenever(accountTimelineStorageDelegate.observeStorageCommand()).thenReturn(Observable.just(command))

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
                  setup(BaseContract.of(NotificationCountChangedCommand::class.java)
                        .result(NotificationCountChangedCommand.NotificationCounterResult(1, 1, 1)))
                  presenter.subscribeNotificationsBadgeUpdates()
                  userNotificationInteractor.notificationCountChangedPipe().send(NotificationCountChangedCommand())

                  verify(view).updateBadgeCount(any())
               }

               it("should refresh feed items on avatar update success") {
                  val updatedUser = User(1)
                  val avatar = User.Avatar()
                  avatar.medium = "updated"
                  updatedUser.avatar = avatar
                  setup(Contract.of(UploadAvatarCommand::class.java).result(USER))

                  presenter.subscribeToAvatarUpdates()
                  profileInteractor.uploadAvatarPipe().send(UploadAvatarCommand(""))

                  verify(presenter).onAvatarUploadSuccess()
               }

               it("should handle error on avatar update error") {
                  setup(Contract.of(UploadAvatarCommand::class.java).exception(RuntimeException()))

                  presenter.subscribeToAvatarUpdates()
                  profileInteractor.uploadAvatarPipe().send(UploadAvatarCommand(""))

                  verify(presenter).handleError(any(), any())
                  verify(presenter).refreshFeedItems()
               }

               it("should call cover upload success when cover is uploaded") {
                  setup(Contract.of(UploadBackgroundCommand::class.java).result(USER))

                  presenter.subscribeToBackgroundUpdates()
                  profileInteractor.uploadBackgroundPipe().send(UploadBackgroundCommand(""))

                  verify(presenter).onCoverUploadSuccess()
               }
            }

            describe("Working with data") {
               describe("Working with feed storage") {
                  it("should refresh timeline when storage is refreshed") {
                     setup()
                     val command = AccountTimelineStorageCommand(null)
                     command.result = emptyList()
                     whenever(accountTimelineStorageDelegate.observeStorageCommand()).thenReturn(Observable.just(command))

                     presenter.subscribeToStorage()

                     verify(presenter).timeLineUpdated(any())
                  }
               }

               describe("Refresh feed") {
                  it("should call refresh feed succeed when getting feed and stop pagination") {
                     setup(Contract.of(GetAccountTimelineCommand.Refresh::class.java).result(emptyList<Any>()))

                     presenter.subscribeRefreshFeeds()
                     feedInteractor.refreshAccountTimelinePipe.send(GetAccountTimelineCommand.Refresh())

                     verify(presenter).refreshFeedSucceed(any())
                  }

                  it("should call refresh feed succeed when getting feed and do not stop pagination") {
                     setup(Contract.of(GetAccountTimelineCommand.Refresh::class.java).result(listOf(TextualPost())))

                     presenter.subscribeRefreshFeeds()
                     feedInteractor.refreshAccountTimelinePipe.send(GetAccountTimelineCommand.Refresh())

                     verify(presenter).refreshFeedSucceed(any())
                  }

                  it("should call refresh feed error and do not stop pagination") {
                     setup(Contract.of(GetAccountTimelineCommand.Refresh::class.java).exception(RuntimeException()))

                     presenter.subscribeRefreshFeeds()
                     feedInteractor.refreshAccountTimelinePipe.send(GetAccountTimelineCommand.Refresh())

                     verify(presenter).refreshFeedError(any(), any())
                  }
               }

               describe("Load next timeline page") {
                  it("should call refresh feed succeed when getting feed and stop pagination") {
                     setup(Contract.of(GetAccountTimelineCommand.LoadNext::class.java).result(emptyList<Any>()))

                     presenter.subscribeLoadNextFeeds()
                     feedInteractor.loadNextAccountTimelinePipe.send(GetAccountTimelineCommand.LoadNext(Date()))

                     verify(presenter).addFeedItems(any())
                  }

                  it("should call refresh feed succeed when getting feed and do not stop pagination") {
                     setup(Contract.of(GetAccountTimelineCommand.LoadNext::class.java).result(listOf(TextualPost())))

                     presenter.subscribeLoadNextFeeds()
                     feedInteractor.loadNextAccountTimelinePipe.send(GetAccountTimelineCommand.LoadNext(Date()))

                     verify(presenter).addFeedItems(any())
                  }

                  it("should call refresh feed error and do not stop pagination") {
                     setup(Contract.of(GetAccountTimelineCommand.LoadNext::class.java).exception(RuntimeException()))

                     presenter.subscribeLoadNextFeeds()
                     feedInteractor.loadNextAccountTimelinePipe.send(GetAccountTimelineCommand.LoadNext(Date()))

                     verify(presenter).loadMoreItemsError(any(), any())
                  }
               }

               describe("Working with uploading operations") {
                  it("should refresh feed with compound operations") {
                     val uploadingOperations = emptyList<Any>()
                     setup(Contract.of(CompoundOperationsCommand::class.java).result(uploadingOperations))

                     presenter.subscribeToBackgroundUploadingOperations()
                     compoundOperationsInteractor.compoundOperationsPipe().send(QueryCompoundOperationsCommand())

                     verify(presenter).refreshFeedItems()
                     assertEquals(presenter.postUploads, uploadingOperations)
                  }
               }
            }
         }
      }

      override fun verifyFeedItemsRefreshedInView() {
         verify(view).refreshFeedItems(any(), any(), anyOrNull())
      }

      override fun verifyFeedItemsNeverRefreshedInView() {
         verify(view, never()).refreshFeedItems(any(), any(), any())
      }

      override fun setup(contract: Contract?) {
         super.setup(contract)
         presenter.user = USER
      }

      override fun onSetupInjector(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         super.onSetupInjector(injector, pipeCreator)
         downloadFileInteractor = DownloadFileInteractor(pipeCreator)
         compoundOperationsInteractor = CompoundOperationsInteractor(pipeCreator, Schedulers.immediate())
         pingAssetStatusInteractor = PingAssetStatusInteractor(pipeCreator)
         authInteractor = AuthInteractor(pipeCreator)
         userNotificationInteractor = UserNotificationInteractor(pipeCreator)
         profileInteractor = ProfileInteractor(pipeCreator, makeSessionHolder(USER_ID))

         injector.registerProvider(DownloadFileInteractor::class.java, { downloadFileInteractor })
         injector.registerProvider(CompoundOperationsInteractor::class.java, { compoundOperationsInteractor })
         injector.registerProvider(ProfileInteractor::class.java, { profileInteractor })
         injector.registerProvider(MediaPickerEventDelegate::class.java, { mediaPickerEventDelegate })
         injector.registerProvider(SocialCropImageManager::class.java, { socialCropImageManager })
         injector.registerProvider(AuthInteractor::class.java, { authInteractor })
         injector.registerProvider(UserNotificationInteractor::class.java, { userNotificationInteractor })
         injector.registerProvider(SnappyRepository::class.java, { snappyDb })
         injector.registerProvider(UploadingPresenterDelegate::class.java, { mock() })
         injector.registerProvider(AccountTimelineStorageDelegate::class.java, { accountTimelineStorageDelegate })
         injector.registerProvider(CachedModelHelper::class.java, { mock() })
      }
   }
}