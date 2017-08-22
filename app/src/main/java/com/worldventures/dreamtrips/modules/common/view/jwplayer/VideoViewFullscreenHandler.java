package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.app.Activity;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.badoo.mobile.util.WeakHandler;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.modules.video.view.custom.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class VideoViewFullscreenHandler {

   private final Activity activity;
   private BackStackDelegate backStackDelegate;
   private final VideoPlayerHolder videoPlayerHolder;
   private boolean isFullscreen;
   private ViewGroup windowedContainer;
   private ViewGroup fullscreenContainer;
   private VideoView videoView;
   private ViewGroup.LayoutParams windowedLayoutParams;
   private ViewGroup.LayoutParams videoContainerWindowedLayoutParams;

   @InjectView(R.id.video_view_fullscreen_button) View fullscreenButton;
   @InjectView(R.id.video_view_container) ViewGroup videoContainer;

   private boolean swapMute = true;

   private WeakHandler weakHandler = new WeakHandler(Looper.getMainLooper());

   public VideoViewFullscreenHandler(Activity activity, BackStackDelegate backStackDelegate,
         VideoPlayerHolder videoPlayerHolder, VideoView videoView) {
      this.activity = activity;
      this.backStackDelegate = backStackDelegate;
      this.videoPlayerHolder = videoPlayerHolder;
      this.videoView = videoView;
      this.windowedContainer = videoView.getWindowedContainer();
      this.fullscreenContainer = videoView.getFullscreenContainer();
   }

   public void initUi() {
      ButterKnife.inject(this, videoView);
      fullscreenButton.setVisibility(View.VISIBLE);
   }

   public void setSwapMute(boolean swapMute) {
      this.swapMute = swapMute;
   }

   private void swapFullscreen() {
      isFullscreen = !isFullscreen;
      if (isFullscreen) onFullscreenRequested();
      else onFullscreenExitRequested();
   }

   public boolean canResizeVideoContainer(int width, int height) {
      if (isFullscreen) {
         videoContainerWindowedLayoutParams.width = width;
         videoContainerWindowedLayoutParams.height = height;
         return false;
      }
      return true;
   }

   private void onFullscreenRequested() {
      Timber.d("Video -- on fullscreen requested");
      hideSystemUI();

      JWPlayerView jwPlayerView = videoPlayerHolder.getJwPlayerView();
      if (jwPlayerView != null) {
         jwPlayerView.destroySurface();
      }
      detachFromWindowedContainer();
      if (jwPlayerView != null) {
         jwPlayerView.initializeSurface();
      }
      weakHandler.post(this::attachToFullscreenContainer);

      refreshFullscreenButton();

      backStackDelegate.setListener(this::onBackPressed);
   }

   private void onFullscreenExitRequested() {
      Timber.d("Video -- on fullscreen exit requested");
      showSystemUI();
      JWPlayerView jwPlayerView = videoPlayerHolder.getJwPlayerView();

      if (jwPlayerView != null) jwPlayerView.destroySurface();
      detachFromFullscreenContainer();
      if (jwPlayerView != null) {
         jwPlayerView.initializeSurface();
      }
      weakHandler.post(this::attachToWindowedContainer);

      backStackDelegate.setListener(null);

      refreshFullscreenButton();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Containers toggles
   ///////////////////////////////////////////////////////////////////////////

   private void detachFromWindowedContainer() {
      windowedLayoutParams = videoView.getLayoutParams();
      videoContainerWindowedLayoutParams = videoContainer.getLayoutParams();
      windowedContainer.removeView(videoView);
   }

   private void attachToWindowedContainer() {
      videoContainer.setLayoutParams(videoContainerWindowedLayoutParams);
      windowedContainer.addView(videoView, windowedLayoutParams);
      if (swapMute && videoPlayerHolder.getJwPlayerView() != null) videoPlayerHolder.getJwPlayerView().setMute(true);
   }

   private void detachFromFullscreenContainer() {
      fullscreenContainer.removeView(videoView);
   }

   private void attachToFullscreenContainer() {
      ViewGroup.LayoutParams params
            = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
      videoContainer.setLayoutParams(params);
      fullscreenContainer.addView(videoView,
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      if (swapMute && videoPlayerHolder.getJwPlayerView() != null) videoPlayerHolder.getJwPlayerView().setMute(false);
   }

   ///////////////////////////////////////////////////////////////////////////
   // System UI visibility
   ///////////////////////////////////////////////////////////////////////////

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

   ///////////////////////////////////////////////////////////////////////////
   // UI related
   ///////////////////////////////////////////////////////////////////////////

   @OnClick(R.id.video_view_fullscreen_button)
   void onFullscreenButtonClick() {
      swapFullscreen();
   }

   private void refreshFullscreenButton() {
      fullscreenButton.setBackgroundResource(isFullscreen
            ? R.drawable.ic_video_fullscreen_collapse : R.drawable.ic_video_fullscreen);
   }

   public boolean isFullscreen() {
      return isFullscreen;
   }

   private boolean onBackPressed() {
      if (videoPlayerHolder.playerExists()) {
         backStackDelegate.setListener(null);
         onFullscreenExitRequested();
         return true;
      }
      return false;
   }
}
