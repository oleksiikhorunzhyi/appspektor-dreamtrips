package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.app.Activity;
import android.view.ViewGroup;

import com.longtailvideo.jwplayer.JWPlayerView;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.modules.video.view.custom.VideoView;

public class VideoPlayerHolder {

   private final DtFullscreenHandler dtFullscreenHandler;

   private JWPlayerView jwPlayerView;
   private VideoContainerView videoContainerView;
   private VideoView videoView;
   private ViewGroup.LayoutParams windowedLayoutParams;

   public VideoPlayerHolder(Activity activity, BackStackDelegate backStackDelegate) {
      this.dtFullscreenHandler = new DtFullscreenHandler(activity, backStackDelegate, this);
   }

   public void init(JWPlayerView jwPlayerView, VideoContainerView videoContainerView, VideoView videoView) {
      this.videoContainerView = videoContainerView;
      this.jwPlayerView = jwPlayerView;
      this.videoView = videoView;

      jwPlayerView.setFullscreenHandler(dtFullscreenHandler);
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

   public void detachFromWindowedContainer() {
      windowedLayoutParams = videoView.getLayoutParams();
      videoContainerView.getRootContainerWhenWindowed().removeView(videoView);
   }

   public void attachToWindowedContainer() {
      videoContainerView.getRootContainerWhenWindowed().addView(videoView, windowedLayoutParams);
   }

   public void detachFromFullscreenContainer() {
      videoContainerView.getRootContainerForFullscreen().removeView(videoView);
   }

   public void attachToFullscreenContainer() {
      videoContainerView.getRootContainerForFullscreen().addView(videoView,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
   }

   public void clearCurrent() {
      jwPlayerView = null;
   }

   public JWPlayerView getJwPlayerView() {
      return jwPlayerView;
   }

   public boolean playerExists() {
      return jwPlayerView != null;
   }
}
