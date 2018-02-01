package com.worldventures.dreamtrips.social.ui.video.view.custom

import com.google.android.exoplayer2.SimpleExoPlayer

interface DTVideoView {
   fun setThumbnail(thumbnail: String)

   fun playVideo(videoConfig: DTVideoConfig)

   fun pauseVideo()

   fun attachPlayer(player: SimpleExoPlayer)

   fun detachPlayer()

   fun showThumbnail()

   fun setVideoConfig(videoConfig: DTVideoConfig)
}
