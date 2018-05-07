package com.worldventures.dreamtrips.social.ui.reptools.service

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.modules.video.service.storage.MediaModelStorage
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.social.ui.reptools.delegate.LocaleVideoDelegate
import com.worldventures.dreamtrips.social.ui.video.presenter.stubLocaleList
import com.worldventures.dreamtrips.social.ui.video.presenter.stubVideoLanguage
import com.worldventures.dreamtrips.social.ui.video.presenter.stubVideoLocale
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.Locale
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("deprecation")
class LocalVideoDelegateSpec : BaseSpec({

   describe("Local video delegate test") {
      init()

      describe("Fetch location and language") {

         it("Should fetch current empty locale and language if locale list is null and cache is empty") {
            val localAndLanguage = localeVideoDelegate.fetchLocaleAndLanguage(context, null)

            assertNull(localAndLanguage.first)
            assertNull(localAndLanguage.second)
         }

         it("Should return default US location if cache is empty and there is detected phone locale, " +
               "which is not included into locale list") {
            val localeList = stubLocaleList()
            configuration.locale = Locale.CHINA
            val localAndLanguage = localeVideoDelegate.fetchLocaleAndLanguage(context, localeList)

            assert(localAndLanguage.first == localeList[0])
            assert(localAndLanguage.second == localeList[0].languages[0])
         }

         it("Should return custom GERMANY locale if cache is empty and there is detected phone locale, " +
               "which exists in local list") {
            val localeList = stubLocaleList(Locale.GERMANY.country)
            configuration.locale = Locale.GERMANY
            val localAndLanguage = localeVideoDelegate.fetchLocaleAndLanguage(context, localeList)

            assert(localAndLanguage.first == localeList[0])
            assert(localAndLanguage.second == localeList[0].languages[0])
         }

         it("Should fetch previously cached locale and language") {
            val videoLanguage = stubVideoLanguage()
            val videoLocale = stubVideoLocale()
            whenever(mediaModelStorage.lastSelectedVideoLanguage).thenReturn(videoLanguage)
            whenever(mediaModelStorage.lastSelectedVideoLocale).thenReturn(videoLocale)

            val localAndLanguage = localeVideoDelegate.fetchLocaleAndLanguage(context, null)

            assert(localAndLanguage.first == videoLocale)
            assert(localAndLanguage.second == videoLanguage)
         }
      }

      describe("Locale temporary cache") {
         it("Should save selected locale and language into database") {
            val videoLanguage = stubVideoLanguage()
            val videoLocale = stubVideoLocale()
            localeVideoDelegate.saveVideoLocaleAndLanguage(videoLocale, videoLanguage)

            verify(mediaModelStorage).saveLastSelectedVideoLanguage(videoLanguage)
            verify(mediaModelStorage).saveLastSelectedVideoLocale(videoLocale)
         }

         it("Should hold loaded locales for the case: do not load them again if language is changed") {
            val localeList = stubLocaleList()
            localeVideoDelegate.saveVideoLocaleList(localeList)

            assertTrue(localeVideoDelegate.fetchVideoLocaleList().containsAll(localeList))
         }
      }
   }

}) {
   companion object {
      lateinit var localeVideoDelegate: LocaleVideoDelegate
      lateinit var mediaModelStorage: MediaModelStorage
      lateinit var context: Context
      lateinit var configuration: Configuration

      fun init() {
         context = mock()
         mediaModelStorage = mock()
         localeVideoDelegate = LocaleVideoDelegate(mediaModelStorage)

         val resources: Resources = mock()
         configuration = Configuration()
         configuration.locale = Locale.US
         whenever(resources.configuration).thenReturn(configuration)
         whenever(context.resources).thenReturn(resources)
      }
   }
}
