package com.worldventures.dreamtrips.social.ui.video.view.custom

import android.os.Bundle
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.modules.common.view.activity.LegacyBaseActivity
import javax.inject.Inject

class FullscreenViewActivity : LegacyBaseActivity() {

   private lateinit var dtVideoView: DTVideoViewImpl
   @Inject lateinit var videoPlayerHolder: VideoPlayerHolder

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_fullscreen_video)
      dtVideoView = findViewById(R.id.player)
      videoPlayerHolder.switchToFullscreenView(dtVideoView, false)

      dtVideoView.fullscreen = true
      dtVideoView.fullscreenExitFunction = { finish() }
   }
}
