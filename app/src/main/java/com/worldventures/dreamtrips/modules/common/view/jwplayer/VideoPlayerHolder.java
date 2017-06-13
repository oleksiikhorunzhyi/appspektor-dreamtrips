package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.app.Activity;
import android.view.ViewGroup;

import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.core.PlayerState;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;

public class VideoPlayerHolder {

   private final DtFullscreenHandler dtFullscreenHandler;

   private JWPlayerView jwPlayerView;
   private VideoAttachmentView container;

   public VideoPlayerHolder(Activity activity, BackStackDelegate backStackDelegate) {
      this.dtFullscreenHandler = new DtFullscreenHandler(activity, backStackDelegate, this);
   }

   public void init(JWPlayerView jwPlayerView, VideoAttachmentView container) {
      this.container = container;
      this.jwPlayerView = jwPlayerView;

      jwPlayerView.setFullscreenHandler(dtFullscreenHandler);
   }

   public void attachToContainer() {
      if (playerExists() && container != null) {
         container.addView(jwPlayerView);
      }
   }

   public void play() {
      if (playerExists()) {
         jwPlayerView.play(true);
      }
   }

   public void dettachFromContainer() {
      if (playerExists() && container != null) {
         container.removeView(jwPlayerView);
      }
   }

   public void clearCurrent() {
      if (container != null) {
         jwPlayerView = null;
         container.clearResources();
      }
   }

   public ViewGroup getContainer() {
      return container;
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
