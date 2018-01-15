package com.worldventures.dreamtrips.social.ui.video.service.command

import android.content.Context
import com.worldventures.core.modules.video.model.Video
import com.worldventures.core.ui.util.ViewUtils
import com.worldventures.janet.injection.InjectableAction
import io.techery.janet.Command
import io.techery.janet.command.annotations.CommandAction
import javax.inject.Inject

@CommandAction
class SortVideo360CategoriesCommand(private val items: List<Any>) : Command<Result>(), InjectableAction {

   @Inject lateinit var context: Context

   @Suppress("all")
   override fun run(callback: CommandCallback<Result>) {
      var allList: List<Any>? = null
      var featuredList: List<Video>? = null
      var recentList: List<Video>? = null

      if (ViewUtils.isLandscapeOrientation(context)) {
         featuredList = items.filter { it is Video }.map { it as Video }.filter { it.isFeatured }.toList()
         recentList = items.filter { it is Video }.map { it as Video }.filter { it.isRecent }.toList()
      } else {
         allList = items
      }

      callback.onSuccess(Result(allList, recentList, featuredList))
   }
}

data class Result(val all: List<Any>?, val recent: List<Video>?, val featured: List<Video>?)
