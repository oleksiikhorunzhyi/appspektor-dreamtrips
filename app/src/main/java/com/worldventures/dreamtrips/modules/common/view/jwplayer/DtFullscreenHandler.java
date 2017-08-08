package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.fullscreen.FullscreenHandler;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;

import timber.log.Timber;

class DtFullscreenHandler implements FullscreenHandler {

   private final VideoPlayerHolder videoPlayerHolder;
   private final Activity activity;
   private BackStackDelegate backStackDelegate;

   DtFullscreenHandler(Activity activity, BackStackDelegate backStackDelegate, VideoPlayerHolder videoPlayerHolder) {
      this.activity = activity;
      this.backStackDelegate = backStackDelegate;
      this.videoPlayerHolder = videoPlayerHolder;
   }

   private void showSystemUI() {
      activity.getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
   }

   private void hideSystemUI() {
      activity.getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_IMMERSIVE);
   }

   @Override
   public void onFullscreenRequested() {
      Timber.d("Video -- on fullscreen requested");
      hideSystemUI();

      JWPlayerView jwPlayerView = videoPlayerHolder.getJwPlayerView();

      jwPlayerView.destroySurface();
      videoPlayerHolder.detachFromWindowedContainer();
      jwPlayerView.initializeSurface();
      jwPlayerView.setMute(false);
      jwPlayerView.post(() -> videoPlayerHolder.attachToFullscreenContainer());

      backStackDelegate.setListener(() -> {
         onBackPressed();
         backStackDelegate.setListener(null);
         return true;
      });
   }

   public boolean onBackPressed() {
      if (videoPlayerHolder.playerExists() && videoPlayerHolder.getJwPlayerView().getFullscreen()) {
         videoPlayerHolder.getJwPlayerView().setFullscreen(false, false);
         return true;
      }
      return false;
   }

   @Override
   public void onFullscreenExitRequested() {
      Timber.d("Video -- on fullscreen exit requested");
      showSystemUI();
      JWPlayerView jwPlayerView = videoPlayerHolder.getJwPlayerView();

      jwPlayerView.destroySurface();
      videoPlayerHolder.detachFromFullscreenContainer();
      jwPlayerView.initializeSurface();
      jwPlayerView.post(() -> {
         videoPlayerHolder.attachToWindowedContainer();
         jwPlayerView.setMute(true);
      });

      backStackDelegate.setListener(null);
   }

   @Override
   public void onResume() {

   }

   @Override
   public void onPause() {

   }

   @Override
   public void onDestroy() {

   }

   @Override
   public void onAllowRotationChanged(boolean b) {

   }

   @Override
   public void updateLayoutParams(ViewGroup.LayoutParams layoutParams) {

   }

   @Override
   public void setUseFullscreenLayoutFlags(boolean b) {

   }
}
