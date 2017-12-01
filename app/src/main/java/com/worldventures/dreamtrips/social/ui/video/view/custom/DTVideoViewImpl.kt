package com.worldventures.dreamtrips.social.ui.video.view.custom

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.worldventures.core.janet.Injector
import com.worldventures.dreamtrips.R
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video
import com.worldventures.dreamtrips.social.ui.tripsimages.delegate.MediaActionPanelInfoInjector
import rx.functions.Action0
import javax.inject.Inject

class DTVideoViewImpl : FrameLayout, DTVideoView {

   @Inject lateinit var videoHolder: VideoPlayerHolder

   private val simpleExoPlayerView: SimpleExoPlayerView
   private val thumbnailView: SimpleDraweeView
   private val fullScreenButton: ImageView
   private val qualitySwitchTextView: TextView
   private val progressBar: ProgressBar
   private val thumbnailViewContainer: FrameLayout
   private val muteButton: ImageView
   private val videoErrorView: TextView

   private val socialInfoContainer: ViewGroup
   private val actionPanelInjector = MediaActionPanelInfoInjector()

   private var videoConfig: DTVideoConfig? = null

   var fullscreen: Boolean = false
      set(value) {
         if (fullscreen) fullScreenButton.setImageResource(R.drawable.ic_video_fullscreen_collapse)
         else fullScreenButton.setImageResource(R.drawable.ic_video_fullscreen)
      }

   var fullscreenExitFunction: () -> Unit = {}

   constructor(context: Context) : super(context)

   constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

   constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

   init {
      val tempContext = context
      if (tempContext is Injector) {
         tempContext.inject(this)
         actionPanelInjector.setup(context, this, tempContext)
      }
      LayoutInflater.from(context).inflate(R.layout.layout_video_view, this, true)
      progressBar = findViewById(R.id.videoLoadingProgress)
      videoErrorView = findViewById(R.id.videoErrorView)
      simpleExoPlayerView = findViewById(R.id.exoPlayerView)
      thumbnailView = findViewById(R.id.thumbnailView)
      thumbnailViewContainer = findViewById(R.id.thumbnailViewContainer)
      fullScreenButton = findViewById(R.id.video_view_fullscreen_button)
      qualitySwitchTextView = findViewById(R.id.video_view_quality_text_view)
      muteButton = findViewById(R.id.video_view_mute_button)
      socialInfoContainer = findViewById(R.id.social_info_container)

      muteButton.setOnClickListener { videoConfig?.let { switchMute(it) } }
      fullScreenButton.setOnClickListener { if (!fullscreen) goFullscreen() else fullscreenExitFunction.invoke() }
      qualitySwitchTextView.setOnClickListener { switchQuality() }
   }

   fun setSocialInfo(video: Video, enableFlagging: Boolean, enableDelete: Boolean) {
      socialInfoContainer.visibility = View.VISIBLE
      actionPanelInjector.setCommentCount(video.commentsCount)
      actionPanelInjector.setLikeCount(video.likesCount)
      actionPanelInjector.setLiked(video.isLiked)
      actionPanelInjector.setOwner(video.owner)
      actionPanelInjector.setPublishedAtDate(video.createdAt)
      actionPanelInjector.enableFlagging(enableFlagging)
      actionPanelInjector.enableEdit(enableDelete)
   }

   override fun setThumbnail(thumbnail: String) {
      thumbnailView.setImageURI(thumbnail)
      progressBar.visibility = View.GONE
   }

   override fun playVideo(videoConfig: DTVideoConfig) {
      setVideoConfig(videoConfig)
      videoHolder.releaseCurrentVideo()
      videoHolder.playVideo(videoConfig)
      videoHolder.attach(this)
   }

   override fun pauseVideo() {
      if (simpleExoPlayerView.player != null) {
         simpleExoPlayerView.player.playWhenReady = false
      }
   }

   override fun attachPlayer(player: SimpleExoPlayer) {
      if (simpleExoPlayerView.player == player) return
      videoErrorView.visibility = View.GONE
      thumbnailViewContainer.visibility = View.GONE
      progressBar.visibility = View.VISIBLE
      simpleExoPlayerView.player = player
      simpleExoPlayerView.player.addVideoListener(object : SimpleExoPlayer.VideoListener {
         override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
         }

         override fun onRenderedFirstFrame() {
            progressBar.visibility = View.GONE
         }
      })
      simpleExoPlayerView.player.addListener(object : PlayerEventListenerAdapter() {
         override fun onPlayerError(error: ExoPlaybackException?) {
            videoErrorView.visibility = View.VISIBLE
         }

         override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
               detachPlayer()
               showThumbnail()
               videoHolder.currentVideoConfig = null
            }
         }
      })
   }

   override fun detachPlayer() {
      videoHolder.currentVideoView = null
      simpleExoPlayerView.player = null
   }

   override fun showThumbnail() {
      thumbnailViewContainer.visibility = View.VISIBLE
   }

   override fun setVideoConfig(videoConfig: DTVideoConfig) {
      this.videoConfig = videoConfig
      videoHolder.currentVideoConfig = videoConfig
      if (videoConfig.mute) muteButton.setImageResource(R.drawable.ic_player_muted)
      else muteButton.setImageResource(R.drawable.ic_player_unmuted)
      qualitySwitchTextView.text = videoConfig.qualities[videoConfig.selectedQualityPosition].name.toUpperCase()
   }

   fun hideFullscreenButton() {
      fullScreenButton.visibility = View.GONE
   }

   private fun goFullscreen() {
      context.startActivity(Intent(context, FullscreenViewActivity::class.java))
   }

   private fun switchMute(videoConfig: DTVideoConfig) {
      if (simpleExoPlayerView.player == null) return

      if (videoConfig.mute) {
         simpleExoPlayerView.player.volume = 1.0f
      } else {
         simpleExoPlayerView.player.volume = 0.0f
      }
      videoConfig.mute = !videoConfig.mute
      setVideoConfig(videoConfig)
      videoHolder.currentVideoConfig = videoConfig
   }

   private fun switchQuality() {
      this.videoConfig?.let {
         val position = simpleExoPlayerView.player.currentPosition
         it.selectedQualityPosition = if (it.selectedQualityPosition == it.qualities.size - 1) 0 else it.selectedQualityPosition + 1
         setVideoConfig(it)
         videoHolder.playVideo(it)
         simpleExoPlayerView.player.seekTo(position)
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Social panel actions
   ///////////////////////////////////////////////////////////////////////////

   fun setThumbnailAction(action: Action0) {
      thumbnailViewContainer.setOnClickListener { action.call() }
   }

   fun setLikeAction(action: Action0) {
      actionPanelInjector.setLikeAction { action.call() }
   }

   fun setCommentAction(action: Action0) {
      actionPanelInjector.setCommentButtonAction { action.call() }
   }

   fun setLikesCountAction(action: Action0) {
      actionPanelInjector.setLikesCountAction(action)
   }

   fun setCommentsCountAction(action: Action0) {
      actionPanelInjector.setCommentsCountAction(action)
   }

   fun setFlagAction(action: Action0) {
      actionPanelInjector.setFlagAction(action)
   }

   fun setEditAction(action: Action0) {
      actionPanelInjector.setEditAction(action)
   }

   fun getEditButton() = actionPanelInjector.editButton
}
