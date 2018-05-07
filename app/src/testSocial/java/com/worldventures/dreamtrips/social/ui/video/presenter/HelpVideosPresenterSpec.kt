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

class HelpVideosPresenterSpec : VideoBasePresenterSpec(HelpVideosTestSuite()) {

   class HelpVideosTestSuite : VideoBaseTestSuite<HelpVideosComponents>(HelpVideosComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Help videos presenter") {

               super.specs().invoke(this)

               it("Take view: should subscribe to locales pipe and listen caching statuses of cached models") {
                  init()
                  linkPresenterAndView()

                  verify(presenter).subscribeToLocalsPipe()
                  verify(presenter).subscribeToCachingStatusUpdates()
               }

               it("On Reload: locales and items should be loaded") {
                  init()
                  linkPresenterAndView()

                  presenter.reload()

                  verify(view).setLocales(any(), any())
                  verify(view).setItems(any())
               }

               it("Changing language: selected language should be saved and items should be updated") {
                  init()
                  linkPresenterAndView()

                  val videoLocale = stubVideoLocale()
                  val videoLanguage = stubVideoLanguage()
                  presenter.onLanguageSelected(videoLocale, videoLanguage)


                  verify(localeVideoDelegate).saveVideoLocaleAndLanguage(videoLocale, videoLanguage)
                  verify(view).setItems(any())
               }

               it("Loading video flow successfully: should load and save locales and notify view about new locales and items") {
                  init()
                  linkPresenterAndView()

                  presenter.onResume()

                  verify(localeVideoDelegate).saveVideoLocaleList(any())
                  verify(view).setLocales(any(), any())
                  verify(view).setItems(any())
               }

               it("Loading video flow, when locales are loaded not successfully: shouldn't load and save locales" +
                     " and notify view about new locales and items") {
                  init(listOf(mockVideosCommand(), mockLocalesCommand(false)))
                  linkPresenterAndView()
                  presenter.onResume()

                  verify(localeVideoDelegate, times(0)).saveVideoLocaleList(any())
                  verify(view, times(0)).setLocales(any(), any())
                  verify(view, times(0)).setItems(any())
               }

               it("Loading video flow, when videos are loaded not successfully: should load and save locales," +
                     "notify view about locales and don't refresh items") {
                  init(listOf(mockVideosCommand(false), mockLocalesCommand()))
                  linkPresenterAndView()
                  presenter.onResume()

                  verify(localeVideoDelegate).saveVideoLocaleList(any())
                  verify(view).setLocales(any(), any())
                  verify(view, times(0)).setItems(any())
               }
            }
         }
      }
   }

   class HelpVideosComponents : VideoBaseComponents<HelpVideosPresenter, HelpVideosPresenter.View>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(HelpVideosPresenter())
         view = mock()

         injector.inject(presenter)
      }
   }
}
