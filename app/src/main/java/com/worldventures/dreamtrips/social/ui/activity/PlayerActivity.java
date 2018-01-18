package com.worldventures.dreamtrips.social.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.social.ui.activity.presenter.PlayerPresenter;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Quality;
import com.worldventures.dreamtrips.social.ui.video.view.custom.DTVideoConfig;
import com.worldventures.dreamtrips.social.ui.video.view.custom.DTVideoViewImpl;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

@Layout(R.layout.player_activity_simple)
public class PlayerActivity extends ActivityWithPresenter<PlayerPresenter> implements PlayerPresenter.View {

   public static final String EXTRA_LAUNCH_COMPONENT = "EXTRA_LAUNCH_COMPONENT";
   public static final String EXTRA_VIDEO_NAME = "EXTRA_VIDEO_NAME";
   public static final String EXTRA_VIDEO_UID = "EXTRA_VIDEO_UID";
   public static final String EXTRA_LANGUAGE = "EXTRA_LANGUAGE";
   private static final long ANALYTIC_CHECKING_INTERVAL = 1000L;

   @InjectView(R.id.myVideo)
   protected DTVideoViewImpl dtVideoView;

   private final PublishSubject<Pair<Long, Long>> videoProgressStream = PublishSubject.create();

   private Uri uri;
   private Subscription subscription;

   @Override
   protected PlayerPresenter createPresentationModel(Bundle savedInstanceState) {
      return new PlayerPresenter((Class) getIntent().getSerializableExtra(EXTRA_LAUNCH_COMPONENT),
            getIntent().getExtras().getString(EXTRA_LANGUAGE), getIntent().getExtras().getString(EXTRA_VIDEO_NAME));
   }

   @Override
   protected void afterCreateView(Bundle savedInstanceState) {
      super.afterCreateView(savedInstanceState);
      uri = getIntent().getData();
      dtVideoView.hideFullscreenButton();
      dtVideoView.setVideoFinishedFunction(() -> {
         videoProgressStream.onNext(new Pair<>(dtVideoView.getVideoDuration(), dtVideoView.getVideoDuration()));
         return null;
      });
      playVideo();
   }

   public void playVideo() {
      if (uri != null) {
         dtVideoView.playVideo(new DTVideoConfig(getIntent().getExtras().getString(EXTRA_VIDEO_UID), false,
               Collections.singletonList(new Quality(uri.toString())), 0));
         listenProgress();
      } else {
         finish();
      }
   }

   private void listenProgress() {
      subscription = Observable.interval(ANALYTIC_CHECKING_INTERVAL, ANALYTIC_CHECKING_INTERVAL, TimeUnit.MILLISECONDS)
            .subscribe(it -> {
               long duration = dtVideoView.getVideoDuration();
               long currentPosition = dtVideoView.getCurrentPosition();
               if (duration > 0) {
                  videoProgressStream.onNext(new Pair<>(currentPosition, duration));
                  if (duration - currentPosition <= ANALYTIC_CHECKING_INTERVAL) {
                     stopListenProgress();
                  }
               }
            });
   }

   private void stopListenProgress() {
      if (subscription != null && !subscription.isUnsubscribed()) {
         subscription.unsubscribe();
      }
   }

   @Override
   public Observable<Pair<Long, Long>> videoProgress() {
      return videoProgressStream.asObservable();
   }

   @Override
   public void onHeadphonesUnPlugged() {
      dtVideoView.pauseVideo();
   }

   @Override
   protected void onPause() {
      super.onPause();
      dtVideoView.pauseVideo();
   }
}
