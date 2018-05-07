package com.worldventures.dreamtrips.social.ui.video.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class PresentationVideosPresenterSpec : VideoBasePresenterSpec(PresentationVideosTestSuite()) {

   class PresentationVideosTestSuite : VideoBaseTestSuite<PresentationVideosComponents>(PresentationVideosComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Presentation videos presenter") {

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
                  verify(view).setItems(any())
               }

               it("Loading videos flow successfully: should update items on view and fetch header") {
                  init()
                  linkPresenterAndView()

                  presenter.onResume()

                  verify(presenter).fetchHeaders(any())
                  verify(view).setItems(any())
               }

               it("Loading videos flow not successfully: shouldn't update items on view and fetch header") {
                  init(listOf(mockVideosCommand(false)))
                  linkPresenterAndView()
                  presenter.onResume()

                  verify(presenter, times(0)).fetchHeaders(any())
                  verify(view, times(0)).setItems(any())
               }
            }
         }
      }
   }

   class PresentationVideosComponents : VideoBaseComponents<PresentationVideosPresenter, PresentationVideosPresenter.View>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(PresentationVideosPresenter())
         view = mock()

         injector.inject(presenter)
      }
   }
}
