package com.worldventures.dreamtrips.social.ui.video.view.custom

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.worldventures.dreamtrips.social.ui.video.DtDataSourceFactory

class VideoPlayerHolder(private val context: Context) {

   var currentVideoView: DTVideoView? = null
   private var fullscreenVideoView: DTVideoView? = null
   var currentVideoConfig: DTVideoConfig? = null

   private val exoPlayer: SimpleExoPlayer

   init {
      val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(DefaultBandwidthMeter()))
      exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
   }

   fun attach(videoView: DTVideoView) {
      currentVideoView = videoView
      videoView.attachPlayer(exoPlayer)
   }

   fun playVideo(currentVideoConfig: DTVideoConfig) {
      this.currentVideoConfig = currentVideoConfig
      exoPlayer.playWhenReady = true
      exoPlayer.volume = if (currentVideoConfig.mute) 0.0f else 1.0f
      exoPlayer.prepare(getMediaSource(currentVideoConfig))
   }

   fun reattachVideoView(videoView: DTVideoView, mute: Boolean) {
      if (currentVideoView == videoView) return
      switchTargetView(currentVideoView, videoView)
      currentVideoView?.showThumbnail()
      currentVideoView = videoView
      currentVideoConfig?.let {
         videoView.setVideoConfig(it.copy(mute = mute))
      }
      exoPlayer.volume = if (mute) 0.0f else 1.0f
      resume()
   }

   fun switchToFullscreenView(fullscreenVideoView: DTVideoView, mute: Boolean) {
      this.fullscreenVideoView = fullscreenVideoView
      currentVideoConfig?.let {
         fullscreenVideoView.setVideoConfig(it.copy(mute = mute))
      }
      switchTargetView(currentVideoView, fullscreenVideoView)
      exoPlayer.volume = if (mute) 0.0f else 1.0f
      resume()
   }

   fun switchFromFullscreen(videoView: DTVideoView, mute: Boolean) {
      currentVideoView = videoView
      currentVideoConfig?.let {
         videoView.setVideoConfig(it.copy(mute = mute))
      }
      switchTargetView(fullscreenVideoView, videoView)
      this.fullscreenVideoView = null
      exoPlayer.volume = if (mute) 0.0f else 1.0f
      resume()
   }

   fun releaseCurrentVideo() {
      currentVideoView?.detachPlayer()
      currentVideoView?.showThumbnail()
      currentVideoView = null
      currentVideoConfig = null
   }

   fun inFullscreen() = fullscreenVideoView != null

   fun resume() {
      exoPlayer.playWhenReady = true
   }

   fun pause() {
      exoPlayer.playWhenReady = false
   }

   private fun getMediaSource(videoConfig: DTVideoConfig) =
         ExtractorMediaSource(
               Uri.parse(videoConfig.qualities[videoConfig.selectedQualityPosition].url),
               DtDataSourceFactory(context),
               DefaultExtractorsFactory(), null, null)

   private fun switchTargetView(oldDTVideoView: DTVideoView?, newDTVideoView: DTVideoView?) {
      // We attach the new view before detaching the old one because this ordering allows the player
      // to swap directly from one surface to another, without transitioning through a state where no
      // surface is attached. This is significantly more efficient and achieves a more seamless
      // transition when using platform provided video decoders.
      newDTVideoView?.attachPlayer(exoPlayer)
      oldDTVideoView?.detachPlayer()
   }

}
