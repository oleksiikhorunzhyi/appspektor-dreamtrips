package com.worldventures.wallet.ui.settings.help.video.impl.language.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.facebook.drawee.view.SimpleDraweeView
import com.worldventures.core.modules.video.model.VideoLocale
import com.worldventures.wallet.R

class VideoLocaleAdapter(context: Context, private val data: List<VideoLocale>) : ArrayAdapter<VideoLocale>(context, 0, data) {

   private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

   override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
      val (view, holder) = prepareHolder(convertView, parent)
      val videoLocale = data[position]

      holder.tvName.setTextColor(Color.BLACK)
      holder.tvName.text = videoLocale.title
      holder.ivFlag.setImageURI(videoLocale.image)
      return view
   }

   private fun prepareHolder(view: View?, parent: ViewGroup): Pair<View, ViewHolder> {
      return if (view == null) {
         val currentView = layoutInflater.inflate(R.layout.item_wallet_video_locale, parent, false)
         val holder = ViewHolder(
               currentView.findViewById(R.id.tv_name),
               currentView.findViewById(R.id.iv_flag))
         currentView.tag = holder
         Pair(currentView, holder)
      } else {
         Pair(view, view.fetchViewHolder())
      }
   }

   private class ViewHolder(
         val tvName: TextView,
         val ivFlag: SimpleDraweeView
   )

   @Suppress("UnsafeCast")
   private fun View.fetchViewHolder(): ViewHolder = tag as ViewHolder
}
