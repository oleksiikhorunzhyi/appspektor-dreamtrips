package com.worldventures.dreamtrips.social.ui.video.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.ui.video.service.command.Result
import com.worldventures.dreamtrips.social.ui.video.service.command.SortVideo360CategoriesCommand
import io.techery.janet.command.test.Contract
import io.techery.janet.command.test.MockCommandActionService
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ThreeSixtyVideosPresenterSpec : VideoBasePresenterSpec(ThreeSixtyVideosTestSuite()) {

   class ThreeSixtyVideosTestSuite : VideoBaseTestSuite<ThreeSixtyVideosComponents>(ThreeSixtyVideosComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("360 videos presenter") {

               super.specs().invoke(this)

               it("Take view: should listen caching statuses of cached models") {
                  init()
                  linkPresenterAndView()

                  verify(presenter).subscribeToVideosPipe()
                  verify(presenter).subscribeToCachingStatusUpdates()
               }

               it("On Reload: should update items with headers") {
                  init()
                  linkPresenterAndView()

                  presenter.reload()

                  verify(presenter).fetchHeaders(any())
                  verify(view).setItems(anyOrNull(), anyOrNull(), anyOrNull())
               }

               it("Loading videos flow successfully: should update items on view and fetch header") {
                  init()
                  linkPresenterAndView()

                  presenter.onResume()

                  verify(presenter).fetchHeaders(any())
                  verify(view).setItems(anyOrNull(), anyOrNull(), anyOrNull())
               }

               it("Loading videos flow not successfully: shouldn't update items on view and fetch header") {
                  init(listOf(mockVideosCommand(false)))
                  linkPresenterAndView()

                  presenter.onResume()

                  verify(presenter, times(0)).fetchHeaders(any())
                  verify(view, times(0)).setItems(anyOrNull(), anyOrNull(), anyOrNull())
               }
            }
         }
      }
   }

   class ThreeSixtyVideosComponents : VideoBaseComponents<ThreeSixtyVideosPresenter, ThreeSixtyVideosPresenter.View>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(ThreeSixtyVideosPresenter())
         view = mock()

         injector.inject(presenter)
      }

      override fun mockActionService(): MockCommandActionService.Builder {
         return super.mockActionService()
               .addContract(Contract.of(SortVideo360CategoriesCommand::class.java).result(Result(null, null, null)))
      }
   }
}
