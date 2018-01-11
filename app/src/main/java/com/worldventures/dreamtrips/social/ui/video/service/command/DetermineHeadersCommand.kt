package com.worldventures.dreamtrips.social.ui.video.service.command

import android.content.Context
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.modules.video.model.VideoCategory
import com.worldventures.core.modules.video.service.storage.MediaModelStorage
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.social.ui.membership.model.MediaHeader
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class DetermineHeadersCommand(val type: HeaderType, val videoCategories: List<VideoCategory>) : Command<List<Any>>(),
      InjectableAction {

   @Inject lateinit var context: Context
   @Inject lateinit var mediaModelStorage: MediaModelStorage

   override fun run(callback: CommandCallback<List<Any>>) {
      val headers: List<Any> = when (type) {
         HeaderType.PRESENTATION -> presentationHeaders()
         HeaderType.TREESIXTY -> treeSixtyHeaders()
         HeaderType.TRAINING -> trainingOrHelpHeaders()
         HeaderType.HELP -> trainingOrHelpHeaders()
      }

      callback.onSuccess(headers)
   }

   private fun presentationHeaders(): ArrayList<Any> {
      val categories = ArrayList<Any>()
      videoCategories.forEach {
         categories.add(MediaHeader(it.category))
         categories.addAll(it.videos)
      }
      return categories
   }

   private fun treeSixtyHeaders(): ArrayList<Any> {
      val categories = java.util.ArrayList<Any>()
      val recentVideos = java.util.ArrayList<Video>()
      val featuredVideos = java.util.ArrayList<Video>()

      videoCategories.forEach {
         recentVideos.addAll(it.videos.filter { it.isRecent }.toList())
         featuredVideos.addAll(it.videos.filter { it.isFeatured }.toList())
      }

      categories.add(MediaHeader(context.getString(R.string.featured_header)))
      categories.addAll(featuredVideos)
      categories.add(MediaHeader(context.getString(R.string.recent_header)))
      categories.addAll(recentVideos)

      return categories
   }

   private fun trainingOrHelpHeaders(): ArrayList<Any> {
      val categories = ArrayList<Any>()
      videoCategories.forEachIndexed { index, category ->
         categories.add(MediaHeader(category.category, index == 0))
         categories.addAll(category.videos)
      }

      if (categories.isNotEmpty()) {
         val firstHeader = categories[0] as MediaHeader
         firstHeader.videoLocale = mediaModelStorage.lastSelectedVideoLocale
         firstHeader.videoLanguage = mediaModelStorage.lastSelectedVideoLanguage
      }

      return categories
   }
}

enum class HeaderType {
   HELP, TRAINING, PRESENTATION, TREESIXTY
}