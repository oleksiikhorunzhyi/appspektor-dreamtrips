package com.worldventures.dreamtrips.modules.common.view.activity;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.PlayerPresenter;

import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import timber.log.Timber;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.widget.media.IjkVideoView;

@Layout(R.layout.player_activity_simple)
public class PlayerActivity extends ActivityWithPresenter<PlayerPresenter> implements PlayerPresenter.View {

   public static final String EXTRA_LAUNCH_COMPONENT = "EXTRA_LAUNCH_COMPONENT";
   public static final String EXTRA_VIDEO_NAME = "EXTRA_VIDEO_NAME";
   public static final String EXTRA_LANGUAGE = "EXTRA_LANGUAGE";
   private static final long ANALYTIC_CHECKING_INTERVAL = 1000;

   @InjectView(R.id.myVideo) protected IjkVideoView videoView;
   @InjectView(R.id.retry) protected TextView retry;
   protected AndroidMediaController mediaController;

   private boolean mBackPressed;

   private Uri uri;
   private Subscription subscription;
   private PublishSubject<Pair<Long, Long>> videoProgressStream = PublishSubject.create();

   @Override
   protected PlayerPresenter createPresentationModel(Bundle savedInstanceState) {
      String videoName = getIntent().getExtras().getString(EXTRA_VIDEO_NAME);
      String language = getIntent().getExtras().getString(EXTRA_LANGUAGE);
      Class parentComponent = (Class) getIntent().getSerializableExtra(EXTRA_LAUNCH_COMPONENT);
      return new PlayerPresenter(parentComponent, language, videoName);
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
      if (!subscription.isUnsubscribed()) subscription.unsubscribe();

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

   @Override
   public Observable<Pair<Long, Long>> videoProgress() {
      return videoProgressStream.asObservable();
   }

   private void playVideo() {
      retry.setVisibility(View.GONE);
      if (uri != null) {
         videoView.setVideoURI(uri);
         videoView.start();
         subscription = listenProgress();
      } else {
         Timber.e("Null Data Source\n");
         finish();
      }
   }

   private Subscription listenProgress() {
      return Observable.interval(ANALYTIC_CHECKING_INTERVAL, ANALYTIC_CHECKING_INTERVAL, TimeUnit.MILLISECONDS)
            .subscribe(o -> {
               long duration = videoView.getDuration();
               long currentPosition = videoView.getCurrentPosition();
               if (duration < 0) return;
               videoProgressStream.onNext(new Pair<>(currentPosition, duration));

               if (duration - currentPosition <= ANALYTIC_CHECKING_INTERVAL) stopListenProgress();
            });
   }

   private void stopListenProgress() {
      if (!subscription.isUnsubscribed()) subscription.unsubscribe();
      videoView.setOnCompletionListener(iMediaPlayer ->
            videoProgressStream.onNext(new Pair<>(iMediaPlayer.getDuration(), iMediaPlayer.getDuration())));
   }

}