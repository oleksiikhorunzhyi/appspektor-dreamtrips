package com.worldventures.dreamtrips.modules.video.view.custom;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;
import com.longtailvideo.jwplayer.core.PlayerState;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;
import com.longtailvideo.jwplayer.media.playlists.MediaSource;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;
import com.messenger.util.CrashlyticsTracker;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.SimpleActivityLifecycleCallbacks;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.FlagView;
import com.worldventures.dreamtrips.modules.common.view.jwplayer.VideoContainerView;
import com.worldventures.dreamtrips.modules.common.view.jwplayer.VideoPlayerHolder;
import com.worldventures.dreamtrips.modules.common.view.jwplayer.VideoViewFullscreenHandler;
import com.worldventures.dreamtrips.modules.common.view.util.VideoDurationFormatter;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;
import com.worldventures.dreamtrips.modules.tripsimages.delegate.MediaActionPanelInfoInjector;
import com.worldventures.dreamtrips.modules.video.utils.mute_strategy.FullscreenMuteStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.State;
import rx.functions.Action0;

public class VideoView extends FrameLayout implements VideoContainerView {
   private static final String TIME_LEFT_FORMAT = "- %s";

   private static final String STREAM_NAME_HD = "HD";
   private static final String STREAM_NAME_SD = "SD";

   @InjectView(R.id.video_view_container) ViewGroup videoContainer;
   @InjectView(R.id.all_info_container) ViewGroup overlayContainer;
   @InjectView(R.id.social_info_container) ViewGroup socialInfoContainer;
   @InjectView(R.id.video_thumbnail_container) ViewGroup videoThumbnailContainer;

   @Nullable private JWPlayerView playerView;
   @InjectView(R.id.video_view_seekbar) SeekBar seekBar;
   @InjectView(R.id.video_view_video_play_pause_button) ImageView playPauseButton;
   @InjectView(R.id.video_view_duration_text_view) TextView timeLeftTextView;
   @InjectView(R.id.video_view_quality_text_view) TextView videoQualityButton;
   @InjectView(R.id.video_thumbnail) SimpleDraweeView videoThumbnail;
   @InjectView(R.id.video_view_mute_button) ImageView muteButton;
   @InjectView(R.id.video_thumbnail_progress) View loadingProgressBar;

   @InjectView(R.id.iv_like) ImageView likeButton;
   @InjectView(R.id.iv_comment) ImageView commentButton;
   @InjectView(R.id.tv_likes_count) TextView likesCount;
   @InjectView(R.id.tv_comments_count) TextView commentsCount;
   @InjectView(R.id.iv_share) ImageView shareButton;
   @InjectView(R.id.flag) FlagView flagButton;
   @InjectView(R.id.edit) ImageView editButton;

   @Inject VideoPlayerHolder videoPlayerHolder;
   @Inject Application application;
   @Inject Activity activity;
   @Inject BackStackDelegate backStackDelegate;
   private VideoViewFullscreenHandler fullscreenHandler;

   @State boolean overlayContainerVisible = true;
   private boolean mute = true;
   private Video video;
   private long duration;
   private String defaultStreamUri;
   private String currentStreamUri;
   private ViewGroup fullscreenContainer;
   private ViewGroup windowedContainer;
   private boolean ignoreProgressUpdates;
   private boolean resizeVideoContainer;

   private MediaActionPanelInfoInjector actionPanelInjector = new MediaActionPanelInfoInjector();

   public VideoView(@NonNull Context context) {
      this(context, null);
   }

   public VideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      inflate(getContext(), R.layout.view_video, this);
      ButterKnife.inject(this);

      Injector injector = (Injector) getContext();
      injector.inject(this);
      actionPanelInjector.setup(getContext(), this, injector);

      initSeekbar();
   }

   private void initSeekbar() {
      // To avoid issues with padding this must be done programmatically
      seekBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.background_video_view_seekbar));
      seekBar.setPadding(0, 0, 0, 0);
      seekBar.setOnSeekBarChangeListener(new SeekbarListener());
   }

   public void setVideo(Video video) {
      setVideo(video, false);
   }

   public void setVideo(Video newVideo, boolean resizeVideoContainer) {
      if (video != null && video.getUploadId().equals(newVideo.getUploadId())
            && resizeVideoContainer == this.resizeVideoContainer) return;

      this.video = newVideo;
      this.defaultStreamUri = video.getHdUrl();
      this.resizeVideoContainer = resizeVideoContainer;
      this.duration = video.getDuration();
      assignVideoInfo();
   }

   public void enableFullscreen(ViewGroup fullscreenContainer, ViewGroup windowedContainer,
         FullscreenMuteStrategy fullscreenMuteStrategy) {
      this.fullscreenContainer = fullscreenContainer;
      this.windowedContainer = windowedContainer;
      if (fullscreenHandler == null) {
         fullscreenHandler = new VideoViewFullscreenHandler(activity, backStackDelegate, videoPlayerHolder, this);
         fullscreenHandler.initUi();
         fullscreenHandler.getFullscreenStateObservable()
               .subscribe(fullscreen -> {
                  boolean newMuteValue = fullscreenMuteStrategy.shouldMute(fullscreenHandler.isFullscreen(), mute);
                  setMute(newMuteValue);
               });
      }
   }

   public void setSocialInfo(Video video, boolean enableFlagging, boolean enableDelete) {
      socialInfoContainer.setVisibility(View.VISIBLE);
      actionPanelInjector.setCommentCount(video.getCommentsCount());
      actionPanelInjector.setLikeCount(video.getLikesCount());
      actionPanelInjector.setLiked(video.isLiked());
      actionPanelInjector.setOwner(video.getOwner());
      actionPanelInjector.setPublishedAtDate(video.getCreatedAt());
      actionPanelInjector.enableFlagging(enableFlagging);
      actionPanelInjector.enableEdit(enableDelete);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Playing video
   ///////////////////////////////////////////////////////////////////////////
   public void play() {
      play(0);
   }

   private void refreshMuteButtonState() {
      muteButton.setImageResource(mute ? R.drawable.ic_player_muted : R.drawable.ic_player_unmuted);
   }

   private void play(long startTimeMillis) {
      if (playerView == null || playerView != videoPlayerHolder.getJwPlayerView()) {
         setupVideoPlayer(startTimeMillis);
      }

      // Workaround to avoid NPE inside JWPlayer. NPE happens if we try to seek right after play, so we must
      // wait until first frame appears instead
      playerView.addOnFirstFrameListener(i -> {
         if (startTimeMillis > 0) {
            seekIgnoreProgressBarUpdates(startTimeMillis);
         } else {
            videoThumbnailContainer.setVisibility(GONE);
            loadingProgressBar.setVisibility(GONE);
         }
      });

      if (playerView != null && playerView.getState() == PlayerState.PLAYING) {
         return;
      }
      loadingProgressBar.setVisibility(VISIBLE);
      showPauseButton();
      videoPlayerHolder.play();
   }

   /**
    * Workaround method to avoid jumping of progress bar position because of the JWPlayer bugs.
    * Ignore all progress bar updates until player starts buffering or playing.
    */
   private void seekIgnoreProgressBarUpdates(long startTimeMillis) {
      ignoreProgressUpdates = true;
      playerView.seek(startTimeMillis);
      VideoPlayerEvents.OnPlayListener onPlayListener = new VideoPlayerEvents.OnPlayListener() {
         @Override
         public void onPlay(PlayerState playerState) {
            if (playerState == PlayerState.BUFFERING || playerState == PlayerState.PLAYING) {
               ignoreProgressUpdates = false;
               videoThumbnailContainer.setVisibility(GONE);
               loadingProgressBar.setVisibility(GONE);
               playerView.removeOnPlayListener(this);
            }
         }
      };
      playerView.addOnPlayListener(onPlayListener);
   }

   private void setupVideoPlayer(long startTimeMillis) {
      //Stop video which playing now, on some neighbour cell
      videoPlayerHolder.clearCurrent();

      setVisibility(VISIBLE);

      boolean resetProgressBar = startTimeMillis == 0;
      clear(resetProgressBar);

      playerView = new JWPlayerView(getContext(), new PlayerConfig.Builder()
            .mute(mute)
            .playlist(preparePlaylist())
            .controls(false)
            .build());

      playerView.addOnDisplayClickListener(this::toggleOverlayContainerVisibility);
      playerView.addOnCompleteListener(() -> {
         clear();
         setupVideoPlayer(0);
      });
      playerView.addOnErrorListener((VideoPlayerEvents.OnErrorListenerV2) event -> {
         CrashlyticsTracker.trackError(new JwPlayerException(event.getMessage(), event.getException()));
         clear();
      });
      playerView.addOnSetupErrorListener(event -> {
         CrashlyticsTracker.trackError(new JwPlayerException(event));
         clear();
      });

      playerView.addOnTimeListener((currentTime, duration) -> {
         if (playerView == null) return;
         if (currentTime > 0) loadingProgressBar.setVisibility(GONE);
         if (ignoreProgressUpdates) return;
         setProgressInSeekbar(currentTime);
         setTimeLeft(currentTime);
      });

      application.registerActivityLifecycleCallbacks(lifecycleCallbacks);

      videoPlayerHolder.init(playerView, this, this);
      videoPlayerHolder.attachJwPlayerToContainer();
   }

   private void setProgressInSeekbar(long currentTime) {
      int currentProgress = Math.round(((float) currentTime / duration) * 100);
      // JWPlayer has a bug, currentTime can be greater than duration
      int progress = Math.min(currentProgress, 100);
      seekBar.setProgress(progress);
   }

   private void setTimeLeft(long currentTime) {
      long timeLeft = duration - currentTime;
      if (timeLeft == duration || timeLeft == 0 || (timeLeft / 1000 < 1)) {
         timeLeftTextView.setText(VideoDurationFormatter.getFormattedDuration(timeLeft));
      } else {
         timeLeftTextView.setText(String.format(TIME_LEFT_FORMAT, VideoDurationFormatter.getFormattedDuration(timeLeft)));
      }
   }

   private List<PlaylistItem> preparePlaylist() {
      List<MediaSource> mediaSources = new ArrayList<>();
      mediaSources.add(new MediaSource.Builder().file(video.getSdUrl())
            .isdefault(defaultStreamUri.equals(video.getSdUrl()))
            .label(STREAM_NAME_SD)
            .build());
      mediaSources.add(new MediaSource.Builder().file(video.getHdUrl())
            .isdefault(defaultStreamUri.equals(video.getHdUrl()))
            .label(STREAM_NAME_HD)
            .build());
      return Collections.singletonList(new PlaylistItem.Builder().sources(mediaSources).build());
   }

   @Override
   public ViewGroup getJwPlayerViewContainer() {
      return videoContainer;
   }

   public void clear() {
      clear(true);
   }

   private void clear(boolean resetProgressBarState) {
      resetUiState(resetProgressBarState);
      application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
      if (playerView == null) return;
      playerView.stop();
      videoPlayerHolder.detachJwPlayerFromContainer();
      playerView = null;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Video info
   ///////////////////////////////////////////////////////////////////////////

   private void assignVideoInfo() {
      setVisibility(VISIBLE);
      setVideoThumbnail();
      refreshDurationInfo();

      boolean showHd = defaultStreamUri.equals(video.getHdUrl());
      videoQualityButton.setText(showHd ? STREAM_NAME_HD : STREAM_NAME_SD);
      currentStreamUri = defaultStreamUri;
   }

   private void refreshDurationInfo() {
      if (duration <= 0) {
         timeLeftTextView.setVisibility(View.INVISIBLE);
         loadingProgressBar.setEnabled(false);
      } else {
         loadingProgressBar.setEnabled(true);
         timeLeftTextView.setVisibility(View.VISIBLE);
         setTimeLeft(0);
      }
   }

   public void resizeView(int width) {
      if (playerView != null) playerView.destroySurface();
      setVideoThumbnailInternal(width);
      if (playerView != null) playerView.initializeSurface();
   }

   private void setVideoThumbnail() {
      videoThumbnailContainer.setVisibility(View.VISIBLE);
      ViewUtils.runTaskAfterMeasure(this, () -> setVideoThumbnailInternal(getWidth()));
   }

   private void setVideoThumbnailInternal(int width) {
      int height = (int) (width / video.getAspectRatio());
      ViewGroup.LayoutParams params = videoThumbnailContainer.getLayoutParams();
      params.height = height;
      videoThumbnailContainer.setLayoutParams(params);
      if (resizeVideoContainer && fullscreenHandler != null && fullscreenHandler.canResizeVideoContainer(width, height)) {
         params = videoContainer.getLayoutParams();
         params.height = height;
         videoContainer.setLayoutParams(params);
      }

      videoThumbnail.setController(GraphicUtils.provideFrescoResizingController(Uri.parse(video.getThumbnail()),
            videoThumbnail.getController(), width, height));
   }

   public void setMute(boolean mute) {
      this.mute = mute;
      refreshMuteButtonState();
      if (playerView != null) playerView.setMute(mute);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Visibility toggles
   ///////////////////////////////////////////////////////////////////////////

   private void toggleOverlayContainerVisibility() {
      overlayContainerVisible = !overlayContainerVisible;
      if (overlayContainerVisible) {
         showOverlayContainer();
      } else {
         hideOverlayContainer();
      }
   }

   private void showOverlayContainer() {
      overlayContainer.setVisibility(VISIBLE);
      muteButton.setVisibility(VISIBLE);
      overlayContainerVisible = true;
   }

   private void hideOverlayContainer() {
      overlayContainer.setVisibility(GONE);
      muteButton.setVisibility(GONE);
      overlayContainerVisible = false;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Player button clicks
   ///////////////////////////////////////////////////////////////////////////

   public ViewGroup getFullscreenContainer() {
      return fullscreenContainer;
   }

   public ViewGroup getWindowedContainer() {
      return windowedContainer;
   }

   @OnClick(R.id.video_view_video_play_pause_button_container)
   void onPlayPauseButtonClick() {
      playVideo();
   }

   public void playVideo() {
      if (playerView == null) {
         play();
         return;
      }
      if (playerView.getState() == PlayerState.PLAYING) {
         videoPlayerHolder.pause();
         showPlayButton();
      } else if (playerView.getState() == PlayerState.PAUSED) {
         videoPlayerHolder.play();
         showPauseButton();
      } else {
         play();
      }
   }

   @OnClick(R.id.video_view_quality_text_view_container)
   void onQualityButtonClick() {
      if (currentStreamUri.equals(video.getHdUrl())) {
         currentStreamUri = video.getSdUrl();
         if (playerView != null) playerView.setCurrentQuality(0);
         videoQualityButton.setText(STREAM_NAME_SD);
      } else {
         currentStreamUri = video.getHdUrl();
         if (playerView != null) playerView.setCurrentQuality(1);
         videoQualityButton.setText(STREAM_NAME_HD);
      }
   }

   @OnClick(R.id.video_thumbnail)
   void onThumbnailTouch() {
      toggleOverlayContainerVisibility();
   }

   private void resetUiState(boolean resetProgressBar) {
      loadingProgressBar.setVisibility(GONE);
      videoThumbnailContainer.setVisibility(VISIBLE);
      if (resetProgressBar) seekBar.setProgress(0);
      refreshDurationInfo();
      showPlayButton();
   }

   public void showPlayButton() {
      playPauseButton.setBackgroundResource(R.drawable.ic_player360_play);
   }

   public void showPauseButton() {
      playPauseButton.setBackgroundResource(R.drawable.ic_player360_pause);
   }

   public void hide() {
      setVisibility(GONE);
   }

   @OnClick(R.id.video_view_mute_button_container)
   void onMuteButtonClick() {
      setMute(!mute);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Social panel actions
   ///////////////////////////////////////////////////////////////////////////

   public void setLikeAction(Action0 action) {
      likeButton.setOnClickListener(v -> action.call());
   }

   public void setCommentAction(Action0 action) {
      commentButton.setOnClickListener(v -> action.call());
   }

   public void setLikesCountAction(Action0 action) {
      likesCount.setOnClickListener(v -> action.call());
   }

   public void setCommentsCountAction(Action0 action) {
      commentsCount.setOnClickListener(v -> action.call());
   }

   public void setShareAction(Action0 action) {
      shareButton.setOnClickListener(v -> action.call());
   }

   public void setFlagAction(Action0 action) {
      flagButton.setOnClickListener(v -> action.call());
   }

   public void setEditAction(Action0 action) {
      editButton.setOnClickListener(v -> action.call());
   }

   public ImageView getEditButton() {
      return editButton;
   }

   private SimpleActivityLifecycleCallbacks lifecycleCallbacks = new SimpleActivityLifecycleCallbacks() {

      @Override
      public void onActivityResumed(Activity activity) {
         super.onActivityResumed(activity);
         if (playerViewIsActive(activity)) {
            playerView.onResume();
         }
      }

      @Override
      public void onActivityPaused(Activity activity) {
         super.onActivityPaused(activity);
         if (playerViewIsActive(activity)) {
            playerView.onPause();
            showPlayButton();
         }
      }

      @Override
      public void onActivityDestroyed(Activity activity) {
         super.onActivityDestroyed(activity);
         if (playerViewIsActive(activity)) {
            playerView.onDestroy();
         }
      }

      private boolean playerViewIsActive(Activity activity) {
         return activity == getContext() && playerView != null;
      }
   };

   private class SeekbarListener implements SeekBar.OnSeekBarChangeListener {

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
         int progress = seekBar.getProgress();
         long positionInMillis = (int) (duration * ((float) progress / 100));
         // Workaround to avoid progress bar jumping after playback has started
         // as WPlayer seeks only to the nearest second
         long positionInSecondsRounded = positionInMillis / 1000 * 1000;
         setProgressInSeekbar(positionInSecondsRounded);
         if (playerView == null || playerView.getState() == PlayerState.IDLE) {
            play(positionInMillis);
         } else {
            seekIgnoreProgressBarUpdates(positionInMillis);
         }
      }

      @Override
      public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }
   }

   public static class JwPlayerException extends IllegalStateException {

      public JwPlayerException(String s) {
         super(s);
      }

      public JwPlayerException(String message, Throwable cause) {
         super(message, cause);
      }

      public JwPlayerException(Throwable cause) {
         super(cause);
      }
   }
}
