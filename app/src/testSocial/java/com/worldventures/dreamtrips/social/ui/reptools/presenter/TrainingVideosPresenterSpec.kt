package com.worldventures.dreamtrips.social.ui.reptools.presenter

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.test.common.Injector
import com.worldventures.dreamtrips.social.ui.video.presenter.VideoBasePresenterSpec
import com.worldventures.dreamtrips.social.ui.video.presenter.mockLocalesCommand
import com.worldventures.dreamtrips.social.ui.video.presenter.mockVideosCommand
import com.worldventures.dreamtrips.social.ui.video.presenter.stubVideoLanguage
import com.worldventures.dreamtrips.social.ui.video.presenter.stubVideoLocale
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TrainingVideosPresenterSpec : VideoBasePresenterSpec(TrainingVideosTestSuite()) {

   class TrainingVideosTestSuite : VideoBaseTestSuite<TrainingVideosComponents>(TrainingVideosComponents()) {

      override fun specs(): SpecBody.() -> Unit = {

         with(components) {
            describe("Training videos presenter") {

               super.specs().invoke(this)

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

   class TrainingVideosComponents : VideoBaseComponents<TrainingVideosPresenter, TrainingVideosPresenter.View>() {

      override fun onInit(injector: Injector, pipeCreator: SessionActionPipeCreator) {
         presenter = spy(TrainingVideosPresenter())
         view = mock()

         injector.inject(presenter)
      }
   }
}
