package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;

import com.longtailvideo.jwplayer.fullscreen.FullscreenHandler;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;

class DtFullscreenHandler implements FullscreenHandler {

   private final VideoPlayerHolder videoPlayerHolder;
   private final Activity activity;

   DtFullscreenHandler(Activity activity, VideoPlayerHolder videoPlayerHolder) {
      this.activity = activity;
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
      hideSystemUI();
      videoPlayerHolder.getJwPlayerView().destroySurface();

      videoPlayerHolder.dettachFromContainer();

      videoPlayerHolder.getJwPlayerView().initializeSurface();

      getRootContainer().post(() -> getRootContainer().addView(videoPlayerHolder.getJwPlayerView()));
   }

   @Override
   public void onFullscreenExitRequested() {
      showSystemUI();
      videoPlayerHolder.getJwPlayerView().destroySurface();

      activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

      dettachFromFullscreenContainer();

      videoPlayerHolder.getJwPlayerView().initializeSurface();

      videoPlayerHolder.getContainer().post(() -> {
         videoPlayerHolder.attachToContainer();
         activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
      });
   }

   private void dettachFromFullscreenContainer() {
      getRootContainer().removeView(videoPlayerHolder.getJwPlayerView());
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

   private ViewGroup getRootContainer() {
      return ButterKnife.findById(activity, R.id.container_details_floating);
   }
}
