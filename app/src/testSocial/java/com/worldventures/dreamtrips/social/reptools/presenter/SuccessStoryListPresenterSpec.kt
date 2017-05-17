package com.worldventures.dreamtrips.social.reptools.presenter

import com.nhaarman.mockito_kotlin.*
import com.techery.spares.utils.delegate.StoryLikedEventDelegate
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryListPresenter
import com.worldventures.dreamtrips.modules.reptools.service.SuccessStoriesInteractor
import com.worldventures.dreamtrips.modules.reptools.service.command.GetSuccessStoriesCommand
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
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
            verify(view, VerificationModeFactory.times(1)).setItems(any())
            verify(view, VerificationModeFactory.times(1)).finishLoading()
         }

         it("should reload in onResume if there are no items") {
            presenter.takeView(view)
            doReturn(0).whenever(view).itemsCount
            presenter.onResume()
            verify(presenter, VerificationModeFactory.times(1)).reload()
         }

         it("should not reload in onResume if there are already some items") {
            presenter.takeView(view)
            doReturn(55).whenever(view).itemsCount
            presenter.onResume()
            verify(presenter, VerificationModeFactory.times(0)).reload()
         }
      }

      context("error response") {
         it ("should handle error") {
            init(BaseContract.of(GetSuccessStoriesCommand::class.java).exception(RuntimeException()))
            presenter.takeView(view)
            doReturn(0).whenever(view).itemsCount
            presenter.onResume()
            verify(presenter, VerificationModeFactory.times(1)).handleError(any(), any())
         }
      }
   }

}) {
   companion object {
      lateinit var presenter: SuccessStoryListPresenter
      lateinit var view: SuccessStoryListPresenter.View

      lateinit var successStoriesInteractor: SuccessStoriesInteractor

      fun init(contract: Contract) {
         presenter = spy(SuccessStoryListPresenter())
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

      fun makeStubStories(): List<SuccessStory> = mutableListOf(makeStubStory(1), makeStubStory(2))

      fun makeStubStory(id: Int): SuccessStory {
         val story = SuccessStory()
         story.id = id
         story.author = "Author $id"
         story.category = "category $id"
         return story;
      }
   }
}
