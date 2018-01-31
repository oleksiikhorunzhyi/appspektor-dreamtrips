package com.worldventures.dreamtrips.social.reptools.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.AssertUtil.assertActionSuccess
import com.worldventures.dreamtrips.social.common.presenter.PresenterBaseSpec
import com.worldventures.dreamtrips.social.service.reptools.SuccessStoriesInteractor
import com.worldventures.dreamtrips.social.service.reptools.command.GetSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.ReadSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.RefreshSuccessStoriesCommand
import com.worldventures.dreamtrips.social.service.reptools.command.SuccessStoriesCommand
import com.worldventures.dreamtrips.social.ui.reptools.model.SuccessStory
import com.worldventures.dreamtrips.social.ui.reptools.presenter.SuccessStoryListPresenter
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import io.techery.janet.command.test.BaseContract
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.mockito.internal.verification.Times
import rx.observers.TestSubscriber

class SuccessStoryListPresenterSpec : PresenterBaseSpec(SuccessStoryListTestSuite()) {

   class SuccessStoryListTestSuite : TestSuite<SuccessStoryListComponents>(SuccessStoryListComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Success stories list presenter") {

               describe("Success response") {
                  beforeEachTest {
                     init(Contract.of(GetSuccessStoriesCommand::class.java).result(makeStubStories()))
                     linkPresenterAndView()
                  }

                  it("Should set items") {
                     presenter.subscribeSuccessStories()
                     successStoriesInteractor.successStoriesPipe.send(RefreshSuccessStoriesCommand(makeStubStories()))

                     verify(view).setItems(any())
                  }

                  it("Should finish loading and should no inform user about anything") {
                     presenter.reload()

                     verify(view).finishLoading()
                     verify(view, Times(0)).informUser(anyOrNull<String>())

                  }

                  it("Should send ReadSuccessStoriesCommand") {
                     val testSubscriber = TestSubscriber<ActionState<SuccessStoriesCommand>>()
                     successStoriesInteractor.successStoriesPipe.observe().subscribe(testSubscriber)
                     presenter.filterFavorites(true)

                     assertActionSuccess(testSubscriber) { it is ReadSuccessStoriesCommand }
                  }

                  it("Should show filter dialog") {
                     presenter.onShowFilterRequired()

                     verify(view).showFilterDialog(false)
                  }
               }

               describe("Error response") {
                  it("Should handle error and inform user about it") {
                     init(BaseContract.of(GetSuccessStoriesCommand::class.java).exception(RuntimeException()))
                     linkPresenterAndView()
                     presenter.reload()

                     verify(view).informUser(anyOrNull<String>())
                     verify(view).finishLoading()
                  }
               }
            }
         }
      }
   }

   class SuccessStoryListComponents : TestComponents<SuccessStoryListPresenter, SuccessStoryListPresenter.View>() {

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

         prepareInjector().apply {
            registerProvider(SuccessStoriesInteractor::class.java, { successStoriesInteractor })
            inject(presenter)
         }
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
