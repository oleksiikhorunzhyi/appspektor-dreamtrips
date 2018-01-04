package com.worldventures.wallet.ui.settings.help.video.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.worldventures.core.modules.video.model.VideoLanguage
import com.worldventures.wallet.R

class VideoLanguagesAdapter(context: Context, private val data: List<VideoLanguage>) : ArrayAdapter<VideoLanguage>(context, 0, data) {

   private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

   override fun getView(position: Int, view: View?, parent: ViewGroup): View {
      val (currentView, holder) = prepareView(view, parent)

      holder.tvName.text = data[position].title

      return currentView
   }

   private fun prepareView(view: View?, parent: ViewGroup): Pair<View, ViewHolder> {
      return if (view == null) {
         val newView = layoutInflater.inflate(R.layout.item_wallet_video_language, parent, false)
         val holder = ViewHolder(newView.findViewById(R.id.tv_name))
         newView.tag = holder
         Pair(newView, holder)
      } else {
         Pair(view, view.tag as ViewHolder)
      }
   }

   private class ViewHolder(val tvName: TextView)
}
