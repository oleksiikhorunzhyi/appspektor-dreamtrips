package com.worldventures.dreamtrips.modules.common.view.activity;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.VideoPlayerPresenter;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import timber.log.Timber;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.widget.media.IjkVideoView;

@Layout(R.layout.player_activity_simple)
public class PlayerActivity extends ActivityWithPresenter<VideoPlayerPresenter> implements VideoPlayerPresenter.View {

   @InjectView(R.id.myVideo) protected IjkVideoView videoView;
   @InjectView(R.id.retry) protected TextView retry;
   protected AndroidMediaController mediaController;

   private boolean mBackPressed;

   Uri uri;

   @Override
   protected VideoPlayerPresenter createPresentationModel(Bundle savedInstanceState) {
      return new VideoPlayerPresenter();
   }

   @Override
   protected void afterCreateView(Bundle savedInstanceState) {
      super.afterCreateView(savedInstanceState);
      uri = getIntent().getData();
      // init player
      IjkMediaPlayer.loadLibrariesOnce(null);

      mediaController = new AndroidMediaController(this, false);

      videoView.setMediaController(mediaController);

      videoView.setOnErrorListener((iMediaPlayer, i, i1) -> {
         Dialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText(getString(R.string.player_error_header))
               .setContentText(getString(R.string.player_error));

         retry.setVisibility(View.VISIBLE);
         sweetAlertDialog.setOnCancelListener(dialog -> finish());
         sweetAlertDialog.show();
         return true;
      });

      playVideo();
   }

   @Override
   protected void onStop() {
      super.onStop();

      if (mBackPressed || !videoView.isBackgroundPlayEnabled()) {
         videoView.stopPlayback();
         videoView.release(true);
         videoView.stopBackgroundPlay();
      } else {
         videoView.enterBackground();
      }
   }

   @Override
   public void onBackPressed() {
      mBackPressed = true;
      super.onBackPressed();
   }

   @Override
   public void onHeadphonesUnPlugged() {
      if (videoView.isPlaying()) {
         videoView.pause();
      }
   }

   @OnClick(R.id.retry)
   void onRetry() {
      playVideo();
   }

   private void playVideo() {
      retry.setVisibility(View.GONE);
      if (uri != null) {
         videoView.setVideoURI(uri);
         videoView.start();
      } else {
         Timber.e("Null Data Source\n");
         finish();
      }
   }
}