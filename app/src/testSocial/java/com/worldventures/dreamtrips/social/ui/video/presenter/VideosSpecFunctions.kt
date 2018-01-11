package com.worldventures.dreamtrips.social.ui.video.presenter

import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.model.VideoCategory
import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.core.modules.video.service.command.GetMemberVideosCommand
import com.worldventures.core.modules.video.service.command.GetVideoLocalesCommand
import io.techery.janet.command.test.Contract
import java.lang.RuntimeException
import java.util.Locale

fun stubLocaleList(country: String = Locale.US.country) = listOf(stubVideoLocale(1, country), stubVideoLocale(2, country))

fun stubVideoLanguageList() = listOf(stubVideoLanguage(1), stubVideoLanguage(2))

fun stubVideoCategoryList() = listOf(stubVideoCategory(1), stubVideoCategory(2))

fun stubVideoList() = listOf(stubVideo(1), stubVideo(2))

fun stubVideo(number: Int = 0): Video {
   return Video("testImageUrl$number", "testVideoUrl$number", "testName$number", "testCategory$number", "testDuration$number", "testLanguage$number")
}

fun stubVideoLanguage(number: Int = 0) = VideoLanguage("TestTitle$number", "TestLocalName$number")

fun stubVideoCategory(number: Int = 0) = VideoCategory("TestCategory$number", stubVideoList())

fun stubVideoLocale(number: Int = 0, country: String = Locale.US.country): VideoLocale {
   return VideoLocale("testTitle$number", country, "testIcon$number", stubVideoLanguageList())
}

fun mockLocalesCommand(successLoadLocals: Boolean = true): Contract =
      when (successLoadLocals) {
         true -> Contract.of(GetVideoLocalesCommand::class.java).result(stubLocaleList())
         false -> Contract.of(GetVideoLocalesCommand::class.java).exception(RuntimeException())
      }

fun mockVideosCommand(successLoadVideos: Boolean = true): Contract =
      when (successLoadVideos) {
         true -> Contract.of(GetMemberVideosCommand::class.java).result(stubVideoCategoryList())
         false -> Contract.of(GetMemberVideosCommand::class.java).exception(RuntimeException())
      }
