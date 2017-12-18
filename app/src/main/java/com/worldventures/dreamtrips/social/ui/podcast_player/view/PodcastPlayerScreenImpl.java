package com.worldventures.dreamtrips.social.ui.podcast_player.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.layout.BaseViewStateLinearLayout;
import com.worldventures.dreamtrips.social.ui.podcast_player.presenter.PodcastPresenter;
import com.worldventures.dreamtrips.social.ui.podcast_player.presenter.PodcastPresenterImpl;
import com.worldventures.dreamtrips.social.ui.podcast_player.view.custom.DtMediaController;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PodcastPlayerScreenImpl extends BaseViewStateLinearLayout<PodcastPlayerScreen, PodcastPresenter> implements PodcastPlayerScreen {

   @InjectView(R.id.player_view) FrameLayout playerView;
   @InjectView(R.id.media_control) DtMediaController mediaController;
   @InjectView(R.id.progress) ProgressBar progressBar;

   private Injector injector;
   private ProgressListener progressListener;

   public PodcastPlayerScreenImpl(Context context) {
      super(context);
      init();
   }

   public PodcastPlayerScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   private void init() {
      injector = ((Injector) getContext());
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      ButterKnife.inject(this);

      mediaController.setOnSeekTo(position -> getPresenter().seekTo(position));
      mediaController.setOnPlayPause(() -> getPresenter().playPause());
   }

   @Override
   public PodcastPresenter createPresenter() {
      return new PodcastPresenterImpl(getContext(), injector, ((Activity) getContext()).getIntent().getData());
   }

   @Override
   public void setProgress(int duration, int currentPosition, int bufferPercentage) {
      mediaController.setEnabled(true);
      mediaController.setDuration(duration);
      mediaController.setProgress(currentPosition, bufferPercentage);

      if (progressBar != null) {
         progressListener.onProgress(duration, currentPosition, bufferPercentage);
      }
   }

   @Override
   public void setPausePlay(boolean isPlaying) {
      mediaController.setEnabled(true);
      progressBar.setVisibility(GONE);
      mediaController.setPausePlay(isPlaying);
   }

   @Override
   public void setPlaybackFailed() {
      mediaController.setVisibility(VISIBLE);
      progressBar.setVisibility(GONE);
   }

   @Override
   public void setPreparing() {
      mediaController.setEnabled(false);
      progressBar.setVisibility(VISIBLE);
   }

   public void setProgressListener(ProgressListener progressListener) {
      this.progressListener = progressListener;
   }

   @Override
   public void informUser(@StringRes int stringId) {
      //
   }

   @Override
   public void informUser(String message) {
      //
   }

   @Override
   public boolean isTabletLandscape() {
      return false;
   }

   public void onBackPressed() {
      getPresenter().onBackPressed();
   }

   @Override
   public void showBlockingProgress() {
      //
   }

   @Override
   public void hideBlockingProgress() {
      //
   }

   public interface ProgressListener {
      void onProgress(int duration, int currentPosition, int bufferPercentage);
   }

}
