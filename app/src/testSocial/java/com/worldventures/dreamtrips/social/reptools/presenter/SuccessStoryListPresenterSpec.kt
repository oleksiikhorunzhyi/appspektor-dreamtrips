package com.worldventures.dreamtrips.social.reptools.presenter

import com.nhaarman.mockito_kotlin.*
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory
import com.worldventures.dreamtrips.social.ui.reptools.presenter.SuccessStoryListPresenter
import com.worldventures.dreamtrips.social.ui.reptools.service.SuccessStoriesInteractor
import com.worldventures.dreamtrips.social.ui.reptools.service.command.GetSuccessStoriesCommand
import com.worldventures.dreamtrips.social.util.event_delegate.StoryLikedEventDelegate
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.VerificationModeFactory

class SuccessStoryListPresenterSpec: PresenterBaseSpec({

   describe("Success stories list presenter") {
      context("success response") {
         beforeEachTest {
            init(Contract.of(GetSuccessStoriesCommand::class.java).result(makeStubStories()))
         }

         it("should set items and finish loading on response") {
            presenter.takeView(view)
            presenter.onResume()

            verify(view).setItems(any())
            verify(view).finishLoading()
         }

         it("should reload in onResume if there are no items") {
            doReturn(0).whenever(view).itemsCount
            presenter.takeView(view)
            presenter.onResume()

            verify(view).setItems(any())
            verify(view).finishLoading()
         }

         it("should not reload in onResume if there are already some items") {
            doReturn(55).whenever(view).itemsCount

            presenter.takeView(view)
            presenter.onResume()

            verify(view, never()).setItems(any())
         }
      }

      context("error response") {
         it ("should handle error") {
            init(BaseContract.of(GetSuccessStoriesCommand::class.java).exception(RuntimeException()))
            doReturn(0).whenever(view).itemsCount

            presenter.takeView(view)
            presenter.onResume()

            verify(view).informUser(anyOrNull<String>())
         }
      }
   }

}) {
   companion object {
      lateinit var presenter: SuccessStoryListPresenter
      lateinit var view: SuccessStoryListPresenter.View

      lateinit var successStoriesInteractor: SuccessStoriesInteractor

      fun init(contract: Contract) {
         presenter = SuccessStoryListPresenter()
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(contract)
         }.build()
         val janet = Janet.Builder().addService(service).build()
         successStoriesInteractor = SuccessStoriesInteractor(SessionActionPipeCreator(janet))

         val injector = prepareInjector()
         injector.registerProvider(StoryLikedEventDelegate::class.java, { StoryLikedEventDelegate() })
         injector.registerProvider(SuccessStoriesInteractor::class.java, { successStoriesInteractor })
         injector.inject(presenter)
         presenter.onInjected()
      }

      fun makeStubStories() = mutableListOf(makeStubStory(1), makeStubStory(2))

      fun makeStubStory(id: Int): SuccessStory {
         return SuccessStory(
               id = id,
               title = "title $id",
               author = "Author $id",
               category = "category $id",
               locale = "",
               url = "",
               sharingUrl = "",
               isLiked = false
         )
      }
   }
}
