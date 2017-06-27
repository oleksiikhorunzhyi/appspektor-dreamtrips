package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.app.Activity;
import android.view.ViewGroup;

import com.longtailvideo.jwplayer.JWPlayerView;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;

public class VideoPlayerHolder {

   private final DtFullscreenHandler dtFullscreenHandler;

   private JWPlayerView jwPlayerView;
   private VideoAttachmentView videoAttachmentView;

   public VideoPlayerHolder(Activity activity, BackStackDelegate backStackDelegate) {
      this.dtFullscreenHandler = new DtFullscreenHandler(activity, backStackDelegate, this);
   }

   public void init(JWPlayerView jwPlayerView, VideoAttachmentView videoAttachmentView) {
      this.videoAttachmentView = videoAttachmentView;
      this.jwPlayerView = jwPlayerView;

      jwPlayerView.setFullscreenHandler(dtFullscreenHandler);
   }

   public void attachToContainer() {
      if (playerExists() && videoAttachmentView != null) {
         videoAttachmentView.getVideoContainer().addView(jwPlayerView);
      }
   }

   public void play() {
      if (playerExists()) {
         jwPlayerView.play(true);
      }
   }

   public void dettachFromContainer() {
      if (playerExists() && videoAttachmentView != null) {
         videoAttachmentView.getVideoContainer().removeView(jwPlayerView);
      }
   }

   public void clearCurrent() {
      if (videoAttachmentView != null) {
         jwPlayerView = null;
         videoAttachmentView.clearResources();
      }
   }

   public ViewGroup getContainer() {
      return videoAttachmentView.getVideoContainer();
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
