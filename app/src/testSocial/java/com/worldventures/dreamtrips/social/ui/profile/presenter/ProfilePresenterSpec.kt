package com.worldventures.dreamtrips.social.ui.profile.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.model.User
import com.worldventures.core.model.session.SessionHolder
import com.worldventures.core.model.session.UserSession
import com.worldventures.core.storage.complex_objects.Optional
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem
import com.worldventures.dreamtrips.social.ui.feed.presenter.delegate.FeedActionHandlerDelegate
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetUserTimelineCommand
import com.worldventures.dreamtrips.social.ui.feed.view.util.TranslationDelegate
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class ProfilePresenterSpec(suite: ProfileTestSuite<ProfileTestComponents<out ProfilePresenter<out ProfilePresenter.View>,
      out ProfilePresenter.View>>) : PresenterBaseSpec(suite) {

   abstract class ProfileTestSuite<out C : ProfileTestComponents<out ProfilePresenter<out ProfilePresenter.View>,
         out ProfilePresenter.View>>(components: C) : PresenterBaseSpec.TestSuite<C>(components) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Profile Presenter") {

               beforeEachTest {
                  init()
                  linkPresenterAndView()
               }

               describe("Base interactions") {

                  it("should refresh items in view on view taken") {
                     val list = ArrayList<FeedItem<out FeedEntity>>()
                     list.add(mock())
                     assertTrue(true)
                     presenter.feedItems = list
                     presenter.restoreItemsInView()

                     verifyFeedItemsRefreshedInView()
                  }

                  it("should not refresh items in view if list is empty") {
                     val list = ArrayList<FeedItem<out FeedEntity>>()
                     presenter.feedItems = list
                     presenter.restoreItemsInView()

                     verifyFeedItemsNeverRefreshedInView()
                  }

                  it("should refresh items in onResume") {
                     presenter.onResume()

                     verify(presenter).refreshFeed()
                  }

                  it("should drop translation delegate view on drop view") {
                     presenter.dropView()

                     verify(translationDelegate).onDropView()
                  }
               }

               describe("Feed actions") {
                  it("should start downloading image") {
                     presenter.onDownloadImage("")

                     verify(feedActionHandlerDelegate).onDownloadImage(any(), any(), any())
                  }

                  it("should load flags") {
                     presenter.onLoadFlags(null)

                     verify(feedActionHandlerDelegate).onLoadFlags(anyOrNull(), any())
                  }

                  it("should flag item") {
                     presenter.onFlagItem(null, 0, null)

                     verify(feedActionHandlerDelegate).onFlagItem(anyOrNull(), any(), anyOrNull(), any(), any())
                  }
               }

               describe("Load feed actions") {
                  it("should correctly refresh") {
                     presenter.onRefresh()

                     verify(presenter).refreshFeed()
                     verify(presenter).loadProfile()
                     verify(view, times(2)).startLoading()
                  }

                  it("should not load next when feed items are empty") {
                     presenter.feedItems = ArrayList<FeedItem<out FeedEntity>>()

                     assertFalse(presenter.onLoadNext())
                  }

                  it("should load next if feeditems are not empty") {
                     val list = getNonEmptyMockedFeedItemsList()
                     presenter.feedItems = list

                     assertTrue(presenter.onLoadNext())
                  }

                  it("should not disable pagination if list is not empty on refreshing feed") {
                     presenter.refreshFeedSucceed(getNonEmptyMockedFeedItemsList())

                     verify(view).updateLoadingStatus(false, false)
                     verify(view).finishLoading()
                  }

                  it("should disable pagination if list is not empty on refreshing feed") {
                     presenter.refreshFeedSucceed(emptyList())

                     verify(view).updateLoadingStatus(false, true)
                     verify(view).finishLoading()
                  }

                  it("should not disable pagination if new page is non empty") {
                     presenter.addFeedItems(getNonEmptyMockedFeedItemsList())

                     verify(view).updateLoadingStatus(false, false)
                  }

                  it("should disable pagination if new page is empty") {
                     presenter.addFeedItems(emptyList())

                     verify(view).updateLoadingStatus(false, true)
                  }

                  it("should handle error and stop loading on refresh feed error") {
                     val command = GetUserTimelineCommand.Refresh(0)
                     presenter.refreshFeedError(command, Exception())

                     verify(presenter).handleError(any(), any())
                     verify(view).updateLoadingStatus(false, false)
                     verify(view).finishLoading()
                  }

                  it("should handle error and stop loading on load more error") {
                     val command = GetUserTimelineCommand.Refresh(0)
                     presenter.loadMoreItemsError(command, Exception())

                     verify(presenter).handleError(any(), any())
                     verify(view).updateLoadingStatus(false, true)
                     verify(view).finishLoading()
                  }
               }
            }
         }
      }

      abstract fun verifyFeedItemsRefreshedInView()

      abstract fun verifyFeedItemsNeverRefreshedInView()
   }

   abstract class ProfileTestComponents<P : ProfilePresenter<V>, V : ProfilePresenter.View> : TestComponents<P, V>() {

      val USER_ID = 1100
      val USER = User(USER_ID)
      val translationDelegate: TranslationDelegate = mock()
      val feedActionHandlerDelegate: FeedActionHandlerDelegate = mock()
      protected val sessionHolder = makeSessionHolder(USER_ID)

      lateinit var feedInteractor: FeedInteractor

      fun getNonEmptyMockedFeedItemsList(): ArrayList<FeedItem<out FeedEntity>> {
         val list = ArrayList<FeedItem<out FeedEntity>>()
         list.add(mock())
         return list
      }

      open fun init(contract: Contract? = null) {
         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            if (contract != null) addContract(contract)
         }.build()

         val janet = Janet.Builder().addService(service).build()
         val sessionPipeCreator = SessionActionPipeCreator(janet)

         feedInteractor = FeedInteractor(sessionPipeCreator)

         val injector = prepareInjector(sessionHolder).apply {
            registerProvider(FeedInteractor::class.java, { feedInteractor })
            registerProvider(TranslationDelegate::class.java, { translationDelegate })
            registerProvider(FeedActionHandlerDelegate::class.java, { feedActionHandlerDelegate })
         }

         onInit(injector, sessionPipeCreator)
      }

      private fun makeSessionHolder(id: Int): SessionHolder {
         val sessionHolder = mock<SessionHolder>()
         val userSession = mock<UserSession>()
         val user = User()
         user.id = id
         whenever(userSession.user()).thenReturn(user)
         whenever(userSession.locale()).thenReturn("mock-locale")
         whenever(userSession.apiToken()).thenReturn("mock-token")
         whenever(userSession.legacyApiToken()).thenReturn("mock-legacy-token")
         whenever(userSession.username()).thenReturn("mock-username")
         whenever(userSession.userPassword()).thenReturn("mock-password")
         whenever(userSession.lastUpdate()).thenReturn(0L)
         whenever(userSession.permissions()).thenReturn(emptyList())
         whenever(sessionHolder.get()).thenReturn(Optional.of(userSession))
         return sessionHolder
      }

      protected abstract fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator)
   }
}
