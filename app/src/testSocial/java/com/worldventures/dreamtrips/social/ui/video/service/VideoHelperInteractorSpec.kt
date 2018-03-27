package com.worldventures.dreamtrips.social.ui.video.service

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.worldventures.core.janet.SessionActionPipeCreator
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.model.VideoCategory
import com.worldventures.core.modules.video.service.storage.MediaModelStorage
import com.worldventures.core.test.AssertUtil
import com.worldventures.dreamtrips.BaseSpec
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader
import com.worldventures.dreamtrips.social.ui.video.presenter.stubVideoLanguage
import com.worldventures.dreamtrips.social.ui.video.presenter.stubVideoLocale
import com.worldventures.dreamtrips.social.ui.video.service.command.DetermineHeadersCommand
import com.worldventures.dreamtrips.social.ui.video.service.command.HeaderType
import com.worldventures.dreamtrips.social.ui.video.service.command.SortVideo360CategoriesCommand
import io.techery.janet.ActionState
import io.techery.janet.CommandActionService
import io.techery.janet.Janet
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import rx.observers.TestSubscriber

class VideoHelperInteractorSpec : BaseSpec({

   describe("Test SortVideo360 command") {

      it("Should return only recent and featured categories if device is in landscape mode") {
         init(true)
         val testSubscriber = TestSubscriber<ActionState<SortVideo360CategoriesCommand>>()

         videoHelperInteractor.sort360VideoPipe
               .createObservable(SortVideo360CategoriesCommand(mockVideoList()))
               .subscribe(testSubscriber)

         AssertUtil.assertActionSuccess(testSubscriber, {
            it.result.all == null
                  && it.result.featured?.all { it.isFeatured } ?: false && it.result.recent?.all { it.isRecent } ?: false
         })
      }

      it("Should return general category if device in portrait mode") {
         init()
         val testSubscriber = TestSubscriber<ActionState<SortVideo360CategoriesCommand>>()

         videoHelperInteractor.sort360VideoPipe
               .createObservable(SortVideo360CategoriesCommand(mockVideoList()))
               .subscribe(testSubscriber)

         AssertUtil.assertActionSuccess(testSubscriber, { it.result.all?.any { it !is Video } ?: false })
      }
   }

   describe("Test Determine headers command") {
      init()

      it("Should return list of videos and headers for presentation video screen") {
         val testSubscriber = TestSubscriber<ActionState<DetermineHeadersCommand>>()

         videoHelperInteractor.headerPipe
               .createObservable(DetermineHeadersCommand(HeaderType.PRESENTATION, stubVideoCategotyList()))
               .subscribe(testSubscriber)

         AssertUtil.assertActionSuccess(testSubscriber, {
            checkHeaderParams(it.result[0], HEADER_TITLE_1)
                  && checkVideoParams(it.result[1], "0")
                  && checkVideoParams(it.result[2], "1")
                  && checkVideoParams(it.result[3], "2")
                  && checkHeaderParams(it.result[4], HEADER_TITLE_2)
                  && checkVideoParams(it.result[5], "3")
                  && checkVideoParams(it.result[6], "4")
                  && checkVideoParams(it.result[7], "5")
         })
      }

      it("Should return list of videos and headers for helper video screen, where first element is header with show language flag ") {
         val testSubscriber = TestSubscriber<ActionState<DetermineHeadersCommand>>()

         videoHelperInteractor.headerPipe
               .createObservable(DetermineHeadersCommand(HeaderType.HELP, stubVideoCategotyList()))
               .subscribe(testSubscriber)

         AssertUtil.assertActionSuccess(testSubscriber, {
            checkHeaderParams(it.result[0], HEADER_TITLE_1, true)
                  && checkVideoParams(it.result[1], "0")
                  && checkVideoParams(it.result[2], "1")
                  && checkVideoParams(it.result[3], "2")
                  && checkHeaderParams(it.result[4], HEADER_TITLE_2)
                  && checkVideoParams(it.result[5], "3")
                  && checkVideoParams(it.result[6], "4")
                  && checkVideoParams(it.result[7], "5")
         })
      }

      it("Should return list of videos and headers for training video screen, where first element is header with show language flag ") {
         val testSubscriber = TestSubscriber<ActionState<DetermineHeadersCommand>>()

         videoHelperInteractor.headerPipe
               .createObservable(DetermineHeadersCommand(HeaderType.TRAINING, stubVideoCategotyList()))
               .subscribe(testSubscriber)

         AssertUtil.assertActionSuccess(testSubscriber, {
            checkHeaderParams(it.result[0], HEADER_TITLE_1, true)
                  && checkVideoParams(it.result[1], "0")
                  && checkVideoParams(it.result[2], "1")
                  && checkVideoParams(it.result[3], "2")
                  && checkHeaderParams(it.result[4], HEADER_TITLE_2)
                  && checkVideoParams(it.result[5], "3")
                  && checkVideoParams(it.result[6], "4")
                  && checkVideoParams(it.result[7], "5")
         })
      }

      it("Should return list of videos and headers for video 360 screen, where first element is header with show language flag ") {
         val testSubscriber = TestSubscriber<ActionState<DetermineHeadersCommand>>()

         videoHelperInteractor.headerPipe
               .createObservable(DetermineHeadersCommand(HeaderType.TREESIXTY, stubVideoCategotyList()))
               .subscribe(testSubscriber)

         AssertUtil.assertActionSuccess(testSubscriber, {
            checkHeaderParams(it.result[0], HEADER_TITLE_1)
                  && checkVideoParams(it.result[1], "0")
                  && checkVideoParams(it.result[2], "3")
                  && checkVideoParams(it.result[3], "5")
                  && checkHeaderParams(it.result[4], HEADER_TITLE_2)
                  && checkVideoParams(it.result[5], "1")
                  && checkVideoParams(it.result[6], "2")
                  && checkVideoParams(it.result[7], "4")
         })
      }
   }

}) {
   companion object {
      val HEADER_TITLE_1 = "Header1"
      val HEADER_TITLE_2 = "Header2"
      lateinit var videoHelperInteractor: VideoHelperInteractor
      lateinit var mediaModelStorage: MediaModelStorage
      lateinit var context: Context

      fun init(landscapeMode: Boolean = false) {
         val daggerCommandActionService = CommandActionService()
               .wrapCache()
               .wrapDagger()
         val janet = Janet.Builder()
               .addService(daggerCommandActionService)
               .build()

         context = mock()
         val resources: Resources = mock()
         val configuration = Configuration()
         configuration.orientation = if (landscapeMode) Configuration.ORIENTATION_LANDSCAPE else Configuration.ORIENTATION_PORTRAIT
         whenever(context.resources).thenReturn(resources)
         whenever(resources.configuration).thenReturn(configuration)
         whenever(context.getString(R.string.featured_header)).thenReturn(HEADER_TITLE_1)
         whenever(context.getString(R.string.recent_header)).thenReturn(HEADER_TITLE_2)

         mediaModelStorage = mock()
         whenever(mediaModelStorage.lastSelectedVideoLocale).thenReturn(stubVideoLocale())
         whenever(mediaModelStorage.lastSelectedVideoLanguage).thenReturn(stubVideoLanguage())

         daggerCommandActionService.registerProvider(Janet::class.java) { janet }
         daggerCommandActionService.registerProvider(Context::class.java) { context }
         daggerCommandActionService.registerProvider(MediaModelStorage::class.java) { mediaModelStorage }

         videoHelperInteractor = VideoHelperInteractor(SessionActionPipeCreator(janet))
      }

      fun checkHeaderParams(category: Any, title: String, showLanguage: Boolean = false): Boolean {
         return category is MediaHeader && category.title == title && category.showLanguage == showLanguage
      }

      fun checkVideoParams(video: Any, name: String) = video is Video && video.videoName == name

      fun stubVideoCategotyList() = listOf(
            VideoCategory(HEADER_TITLE_1, listOf(mockVideo(true, "0"), mockVideo(name = "1"), mockVideo(name = "2"))),
            VideoCategory(HEADER_TITLE_2, listOf(mockVideo(true, "3"), mockVideo(name = "4"), mockVideo(true, name = "5")))
      )

      fun mockVideoList() = listOf(
            MediaHeader("Featured"),
            mockVideo(true), mockVideo(true), mockVideo(true), mockVideo(true),
            MediaHeader("Recent"),
            mockVideo(false), mockVideo(), mockVideo(), mockVideo(), mockVideo()
      )

      fun mockVideo(featured: Boolean = false, name: String = "test"): Video {
         val video: Video = mock()
         whenever(video.isFeatured).thenReturn(featured)
         whenever(video.isRecent).thenReturn(!featured)
         whenever(video.videoName).thenReturn(name)
         return video
      }
   }
}
