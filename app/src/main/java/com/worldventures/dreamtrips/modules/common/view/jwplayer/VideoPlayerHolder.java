package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.view.ViewGroup;

import com.longtailvideo.jwplayer.JWPlayerView;
import com.worldventures.dreamtrips.social.ui.video.view.custom.VideoView;

public class VideoPlayerHolder {

   private JWPlayerView jwPlayerView;
   private VideoContainerView videoContainerView;
   private VideoView videoView;

   public void init(JWPlayerView jwPlayerView, VideoContainerView videoContainerView, VideoView videoView) {
      this.videoContainerView = videoContainerView;
      this.jwPlayerView = jwPlayerView;
      this.videoView = videoView;
   }

   public void play() {
      if (playerExists()) {
         jwPlayerView.play(true);
      }
   }

   public void pause() {
      if (playerExists()) {
         jwPlayerView.pause();
      }
   }

   public void attachJwPlayerToContainer() {
      if (playerExists() && videoContainerView != null) {
         videoContainerView.getJwPlayerViewContainer().addView(jwPlayerView,
               new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      }
   }

   public void detachJwPlayerFromContainer() {
      if (playerExists() && videoContainerView != null) {
         videoContainerView.getJwPlayerViewContainer().removeView(jwPlayerView);
      }
   }

   public void clearCurrent() {
      if (videoView != null) {
         videoView.clear();
      }
      videoView = null;
      jwPlayerView = null;
   }

   public JWPlayerView getJwPlayerView() {
      return jwPlayerView;
   }

   public boolean playerExists() {
      return jwPlayerView != null;
   }
}
