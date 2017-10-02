package com.worldventures.dreamtrips.social.ui.profile.presenter

import com.messenger.delegate.StartChatDelegate
import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.Circle
import com.worldventures.dreamtrips.common.Injector
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.NotificationFeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetUserTimelineCommand
import com.worldventures.dreamtrips.social.ui.feed.storage.command.UserTimelineStorageCommand
import com.worldventures.dreamtrips.social.ui.feed.storage.delegate.UserTimelineStorageDelegate
import com.worldventures.dreamtrips.social.ui.friends.service.CirclesInteractor
import com.worldventures.dreamtrips.social.ui.friends.service.FriendsInteractor
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor
import com.worldventures.dreamtrips.social.ui.profile.service.command.AddFriendToCircleCommand
import com.worldventures.dreamtrips.social.ui.profile.service.command.RemoveFriendFromCircleCommand
import io.techery.janet.command.test.Contract
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.Observable
import java.util.*

class UserPresenterSpec : ProfilePresenterSpec(UserPresenterTestBody()) {

   class UserPresenterTestBody : TestBody<UserPresenter, UserPresenter.View>() {
      val CIRCLE = Circle.withTitle("Friends")

      lateinit var circlesInteractor: CirclesInteractor
      lateinit var friendsInteractor: FriendsInteractor
      lateinit var notificationFeedInteractor: NotificationFeedInteractor
      val notificationDelegate: NotificationDelegate = mock()
      val startChatDelegate: StartChatDelegate = mock()
      lateinit var profileInteractor: ProfileInteractor
      val userTimelineStorageDelegate: UserTimelineStorageDelegate = mock()

      override fun getDescription(): String = "Account Presenter"

      override fun makePresenter() = UserPresenter(UserBundle(USER))

      override fun makeView(): UserPresenter.View = mock()

      override fun makeExtendedSuite(): SpecBody.() -> Unit {
         return {
            describe("Basic interactions") {
               beforeEachTest {
                  setup()
               }

               it("should cancel notification on injected") {
                  presenter.onInjected()
                  verify(notificationDelegate).cancel(any<Int>())
               }

               it("should subscribe to data sources on view taken") {
                  val userTimelineStorageCommand: UserTimelineStorageCommand = mock()
                  whenever(userTimelineStorageDelegate.observeStorageCommand()).thenReturn(Observable.just(userTimelineStorageCommand))
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
                  setup()
                  val command = UserTimelineStorageCommand(0, null)
                  command.result = getNonEmptyMockedFeedItemsList()
                  whenever(userTimelineStorageDelegate.observeStorageCommand()).thenReturn(Observable.just(command))

                  presenter.subscribeToStorage()

                  verify(presenter).onItemsChanged(any())
               }

               it("should call refresh feed succeed on success") {
                  setup(Contract.of(GetUserTimelineCommand.Refresh::class.java).result(emptyList<Any>()))

                  presenter.subscribeRefreshFeeds()
                  feedInteractor.refreshUserTimelinePipe.send(GetUserTimelineCommand.Refresh(USER_ID))

                  verify(presenter).refreshFeedSucceed(any())
               }

               it("should call refresh feed error on fail") {
                  setup(Contract.of(GetUserTimelineCommand.Refresh::class.java).exception(RuntimeException()))

                  presenter.subscribeRefreshFeeds()
                  feedInteractor.refreshUserTimelinePipe.send(GetUserTimelineCommand.Refresh(USER_ID))

                  verify(presenter).refreshFeedError(any(), any())
               }

               it("should call add feed items on load more success") {
                  setup(Contract.of(GetUserTimelineCommand.LoadNext::class.java).result(emptyList<Any>()))

                  presenter.subscribeLoadNextFeeds()
                  feedInteractor.loadNextUserTimelinePipe.send(GetUserTimelineCommand.LoadNext(USER_ID, Date()))

                  verify(presenter).addFeedItems(any())
               }

               it("should call refresh feed error on fail") {
                  setup(Contract.of(GetUserTimelineCommand.LoadNext::class.java).exception(RuntimeException()))

                  presenter.subscribeRefreshFeeds()
                  feedInteractor.loadNextUserTimelinePipe.send(GetUserTimelineCommand.LoadNext(USER_ID, Date()))

                  verify(presenter).loadMoreItemsError(any(), any())
               }
            }

            describe("Circles interactions") {
               it("should refresh view when user is added to friends") {
                  setup(Contract.of(AddFriendToCircleCommand::class.java).result(USER_ID))

                  presenter.subscribeToChangingCircles()
                  profileInteractor.addFriendToCirclesPipe().send(AddFriendToCircleCommand(CIRCLE, USER))

                  assert(USER.circles.contains(CIRCLE))
                  verify(presenter).refreshFeedItems()
               }

               it("should refresh view when user is removed from friends") {
                  setup(Contract.of(RemoveFriendFromCircleCommand::class.java).result(USER_ID))
                  USER.circles.add(CIRCLE)

                  presenter.subscribeToChangingCircles()
                  profileInteractor.removeFriendFromCirclesPipe().send(RemoveFriendFromCircleCommand(CIRCLE, USER))

                  assert(USER.circles.isEmpty())
                  verify(presenter).refreshFeedItems()
               }
            }
         }
      }

      override fun verifyFeedItemsRefreshedInView() {
         verify(view).refreshFeedItems(any(), any())
      }

      override fun verifyFeedItemsNeverRefreshedInView() {
         verify(view, never()).refreshFeedItems(any(), any())
      }

      override fun setup(contract: Contract?) {
         super.setup(contract)
         USER.circles = mutableListOf()
      }

      override fun onSetupInjector(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         super.onSetupInjector(injector, pipeCreator)
         circlesInteractor = CirclesInteractor(pipeCreator)
         friendsInteractor = FriendsInteractor(pipeCreator)
         notificationFeedInteractor = NotificationFeedInteractor(pipeCreator)
         profileInteractor = ProfileInteractor(pipeCreator, sessionHolder)

         injector.registerProvider(CirclesInteractor::class.java, { circlesInteractor })
         injector.registerProvider(FriendsInteractor::class.java, { friendsInteractor })
         injector.registerProvider(NotificationFeedInteractor::class.java, { notificationFeedInteractor })
         injector.registerProvider(ProfileInteractor::class.java, { profileInteractor })

         injector.registerProvider(NotificationDelegate::class.java, { notificationDelegate })
         injector.registerProvider(UserTimelineStorageDelegate::class.java, { userTimelineStorageDelegate })
         injector.registerProvider(StartChatDelegate::class.java, { startChatDelegate })
      }
   }
}