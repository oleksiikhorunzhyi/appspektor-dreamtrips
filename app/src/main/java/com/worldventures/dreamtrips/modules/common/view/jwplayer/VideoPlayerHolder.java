package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.app.Activity;
import android.view.ViewGroup;

import com.longtailvideo.jwplayer.JWPlayerView;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;

public class VideoPlayerHolder {

   private final DtFullscreenHandler dtFullscreenHandler;

   private JWPlayerView jwPlayerView;
   private VideoContainerView videoContainerView;

   public VideoPlayerHolder(Activity activity, BackStackDelegate backStackDelegate) {
      this.dtFullscreenHandler = new DtFullscreenHandler(activity, backStackDelegate, this);
   }

   public void init(JWPlayerView jwPlayerView, VideoContainerView videoContainerView) {
      this.videoContainerView = videoContainerView;
      this.jwPlayerView = jwPlayerView;

      jwPlayerView.setFullscreenHandler(dtFullscreenHandler);
   }

   public void attachToContainer() {
      if (playerExists() && videoContainerView != null) {
         videoContainerView.getVideoContainer().addView(jwPlayerView);
      }
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

   public void dettachFromContainer() {
      if (playerExists() && videoContainerView != null) {
         videoContainerView.getVideoContainer().removeView(jwPlayerView);
      }
   }

   public void clearCurrent() {
      if (videoContainerView != null) {
         jwPlayerView = null;
      }
   }

   public ViewGroup getContainer() {
      return videoContainerView.getVideoContainer();
   }

   public JWPlayerView getJwPlayerView() {
      return jwPlayerView;
   }

   public boolean onBackPressed() {
      if (playerExists() && jwPlayerView.getFullscreen()) {
         jwPlayerView.setFullscreen(false, false);
         return true;
      }
      return false;
   }

   private boolean playerExists() {
      return jwPlayerView != null;
   }
}
