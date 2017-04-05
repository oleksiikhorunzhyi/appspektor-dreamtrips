package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.app.Activity;
import android.view.ViewGroup;

import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.core.PlayerState;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

import timber.log.Timber;

public class VideoPlayerHolder {

   private final DtFullscreenHandler dtFullscreenHandler;
   private final Activity activity;

   private JWPlayerView jwPlayerView;
   private ViewGroup container;


   public VideoPlayerHolder(Activity activity) {
      this.activity = activity;
      this.dtFullscreenHandler = new DtFullscreenHandler(activity, this);
   }

   public void init(JWPlayerView jwPlayerView, ViewGroup container) {
      this.container = container;
      this.jwPlayerView = jwPlayerView;

      jwPlayerView.setFullscreenHandler(dtFullscreenHandler);
   }

   public void onConfigurationChangedToLandscape() {
      if (playerExists() && jwPlayerView.getState() == PlayerState.PLAYING) {
         jwPlayerView.setFullscreen(true, true);
      }
   }

   public void attachToContainer() {
      if (playerExists() && container != null) {
         container.addView(jwPlayerView);
      }
   }

   public void play() {
      if (playerExists()) {
         if (ViewUtils.isLandscapeOrientation(activity)) {
            jwPlayerView.setFullscreen(true, true);
         }
         jwPlayerView.play(true);
      }
   }

   public void dettachFromContainer() {
      if (playerExists() && container != null) {
         container.removeView(jwPlayerView);
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

   public void onResume() {
      if (playerExists()) {
         jwPlayerView.onResume();
      }
   }

   public void onPause() {
      if (playerExists()) {
         jwPlayerView.onPause();
      }
   }

   public void onDestroy() {
      if (playerExists()) {
         jwPlayerView.onDestroy();
      }
   }

   public boolean playerExists() {
      return jwPlayerView != null;
   }
}
