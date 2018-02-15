package com.worldventures.dreamtrips.social.ui.profile.presenter

import com.messenger.delegate.StartChatDelegate
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.Circle
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate
import com.worldventures.dreamtrips.social.service.profile.ProfileInteractor
import com.worldventures.dreamtrips.social.service.profile.command.AddFriendToCircleCommand
import com.worldventures.dreamtrips.social.service.profile.command.RemoveFriendFromCircleCommand
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.NotificationFeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetUserTimelineCommand
import com.worldventures.dreamtrips.social.ui.feed.storage.command.UserTimelineStorageCommand
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.UserTimelineStorageDelegate
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle
import io.techery.janet.command.test.Contract
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.Observable
import java.util.ArrayList
import java.util.Date

class UserPresenterSpec : ProfilePresenterSpec(UserTestSuite()) {

   class UserTestSuite : ProfileTestSuite<UserTestComponents>(UserTestComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("User Presenter") {

               super.specs().invoke(this)

               describe("Basic interactions") {
                  beforeEachTest {
                     init()
                     linkPresenterAndView()
                  }

                  it("should cancel notification on injected") {
                     presenter.onInjected()
                     verify(notificationDelegate).cancel(any<Int>())
                  }

                  it("should subscribe to data sources on view taken") {
                     val userTimelineStorageCommand: UserTimelineStorageCommand = mock()
                     whenever(userTimelineStorageDelegate.observeStorageCommand()).thenReturn(Observable.just(userTimelineStorageCommand))
                     val presenter = presenter
                     presenter.feedItems = ArrayList()
                     presenter.onViewTaken()

                     verify(presenter).subscribeToStorage()
                     verify(presenter).subscribeLoadNextFeeds()
                     verify(presenter).subscribeRefreshFeeds()
                     verify(presenter).subscribeToChangingCircles()
                  }
               }

               describe("Interaction with data") {
                  it("should refresh items when getting storage command") {
                     init()
                     linkPresenterAndView()

                     val command = UserTimelineStorageCommand(0, null)
                     command.result = getNonEmptyMockedFeedItemsList()
                     whenever(userTimelineStorageDelegate.observeStorageCommand()).thenReturn(Observable.just(command))

                     presenter.subscribeToStorage()

                     verify(presenter).onItemsChanged(any())
                  }

                  it("should call refresh feed succeed on success") {
                     init(Contract.of(GetUserTimelineCommand.Refresh::class.java).result(emptyList<Any>()))
                     linkPresenterAndView()

                     presenter.subscribeRefreshFeeds()
                     feedInteractor.refreshUserTimelinePipe.send(GetUserTimelineCommand.Refresh(USER_ID))

                     verify(presenter).refreshFeedSucceed(any())
                  }

                  it("should call refresh feed error on fail") {
                     init(Contract.of(GetUserTimelineCommand.Refresh::class.java).exception(RuntimeException()))
                     linkPresenterAndView()

                     presenter.subscribeRefreshFeeds()
                     feedInteractor.refreshUserTimelinePipe.send(GetUserTimelineCommand.Refresh(USER_ID))

                     verify(presenter).refreshFeedError(any(), any())
                  }

                  it("should call add feed items on load more success") {
                     init(Contract.of(GetUserTimelineCommand.LoadNext::class.java).result(emptyList<Any>()))
                     linkPresenterAndView()

                     presenter.subscribeLoadNextFeeds()
                     feedInteractor.loadNextUserTimelinePipe.send(GetUserTimelineCommand.LoadNext(USER_ID, Date()))

                     verify(presenter).addFeedItems(any())
                  }

                  it("should call refresh feed error on fail") {
                     init(Contract.of(GetUserTimelineCommand.LoadNext::class.java).exception(RuntimeException()))
                     linkPresenterAndView()

                     presenter.subscribeRefreshFeeds()
                     feedInteractor.loadNextUserTimelinePipe.send(GetUserTimelineCommand.LoadNext(USER_ID, Date()))

                     verify(presenter).loadMoreItemsError(any(), any())
                  }
               }

               describe("Circles interactions") {
                  it("should refresh view when user is added to friends") {
                     init(Contract.of(AddFriendToCircleCommand::class.java).result(USER_ID))
                     linkPresenterAndView()

                     presenter.subscribeToChangingCircles()
                     profileInteractor.addFriendToCirclePipe.send(AddFriendToCircleCommand(CIRCLE, USER))

                     assert(USER.circles.contains(CIRCLE))
                     verify(presenter).refreshFeedItems()
                  }

                  it("should refresh view when user is removed from friends") {
                     init(Contract.of(RemoveFriendFromCircleCommand::class.java).result(USER_ID))
                     linkPresenterAndView()
                     USER.circles.add(CIRCLE)

                     presenter.subscribeToChangingCircles()
                     profileInteractor.removeFriendFromCirclePipe
                           .send(RemoveFriendFromCircleCommand(CIRCLE, USER))

                     assert(USER.circles.isEmpty())
                     verify(presenter).refreshFeedItems()
                  }
               }
            }
         }
      }

      override fun verifyFeedItemsRefreshedInView() {
         verify(components.view).refreshFeedItems(any(), any())
      }

      override fun verifyFeedItemsNeverRefreshedInView() {
         verify(components.view, never()).refreshFeedItems(any(), any())
      }
   }

   class UserTestComponents : ProfileTestComponents<UserPresenter, UserPresenter.View>() {

      val CIRCLE = Circle.withTitle("Friends")
      val notificationDelegate: NotificationDelegate = mock()
      val userTimelineStorageDelegate: UserTimelineStorageDelegate = mock()

      lateinit var profileInteractor: ProfileInteractor

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(UserPresenter(UserBundle(USER)))
         view = mock()
         USER.circles = mutableListOf()

         val circlesInteractor = CirclesInteractor(pipeCreator)
         val friendsInteractor = FriendsInteractor(pipeCreator)
         val notificationFeedInteractor = NotificationFeedInteractor(pipeCreator)
         profileInteractor = ProfileInteractor(pipeCreator, sessionHolder)

         injector.apply {
            registerProvider(CirclesInteractor::class.java, { circlesInteractor })
            registerProvider(FriendsInteractor::class.java, { friendsInteractor })
            registerProvider(NotificationFeedInteractor::class.java, { notificationFeedInteractor })
            registerProvider(ProfileInteractor::class.java, { profileInteractor })

            registerProvider(NotificationDelegate::class.java, { notificationDelegate })
            registerProvider(UserTimelineStorageDelegate::class.java, { userTimelineStorageDelegate })
            registerProvider(StartChatDelegate::class.java, { mock() })

            inject(presenter)
         }
      }
   }
}
