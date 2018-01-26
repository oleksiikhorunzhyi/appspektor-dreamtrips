package com.worldventures.dreamtrips.social.reptools.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
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
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class SuccessStoryListPresenterSpec : PresenterBaseSpec(SuccessStoryListTestSuite()) {

   class SuccessStoryListTestSuite : TestSuite<SuccessStoryListComponents>(SuccessStoryListComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Success stories list presenter") {

               context("success response") {
                  beforeEachTest {
                     init(Contract.of(GetSuccessStoriesCommand::class.java).result(makeStubStories()))
                     linkPresenterAndView()
                  }

                  it("should set items and finish loading on response") {
                     presenter.onResume()

                     verify(view).setItems(any())
                     verify(view).finishLoading()
                  }

                  it("should reload in onResume if there are no items") {
                     val view = view
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
                  it("should handle error") {
                     init(BaseContract.of(GetSuccessStoriesCommand::class.java).exception(RuntimeException()))
                     linkPresenterAndView()
                     doReturn(0).whenever(view).itemsCount

                     presenter.takeView(view)
                     presenter.onResume()

                     verify(view).informUser(anyOrNull<String>())
                  }
               }
            }
         }
      }
   }

   class SuccessStoryListComponents : TestComponents<SuccessStoryListPresenter, SuccessStoryListPresenter.View>() {

      fun init(contract: Contract) {
         presenter = SuccessStoryListPresenter()
         view = mock()

         val service = MockCommandActionService.Builder().apply {
            actionService(CommandActionService())
            addContract(contract)
         }.build()
         val janet = Janet.Builder().addService(service).build()
         val successStoriesInteractor = SuccessStoriesInteractor(SessionActionPipeCreator(janet))

         prepareInjector().apply {
            registerProvider(StoryLikedEventDelegate::class.java, { StoryLikedEventDelegate() })
            registerProvider(SuccessStoriesInteractor::class.java, { successStoriesInteractor })
            inject(presenter)
         }
      }

      fun makeStubStories() = mutableListOf(makeStubStory(1), makeStubStory(2))

      private fun makeStubStory(id: Int): SuccessStory {
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
