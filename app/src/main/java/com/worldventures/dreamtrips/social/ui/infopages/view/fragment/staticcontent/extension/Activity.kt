package com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.extension

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager

fun Activity.handleFullScreen(fullscreen: Boolean) {
   val supportActionBar = (this as? AppCompatActivity)?.supportActionBar
   window.apply {
      if (fullscreen) {
         supportActionBar?.hide()
         addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
               or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
         clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
      } else {
         supportActionBar?.show()
         addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
         clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
               or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
         decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
      }
   }
}

