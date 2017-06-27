package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;
import com.longtailvideo.jwplayer.events.ErrorEvent;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;
import com.longtailvideo.jwplayer.media.playlists.MediaSource;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;
import com.techery.spares.ui.activity.InjectingActivity;
import com.techery.spares.utils.SimpleActivityLifecycleCallbacks;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class VideoAttachmentView extends FrameLayout {

   private static final String SKIN_URL = "file:///android_asset/player_skin.css";

   private Video video;

   @Inject VideoPlayerHolder videoPlayerHolder;
   @Inject Application application;

   @InjectView(R.id.videoThumbnail) SimpleDraweeView videoThumbnail;
   @InjectView(R.id.videoThumbnailContainer) FrameLayout videoThumbnailContainer;
   @InjectView(R.id.playButton) ImageView playButton;
   @InjectView(R.id.playProgress) ProgressBar playProgressBar;
   @InjectView(R.id.videoContainer) FrameLayout videoContainer;

   private JWPlayerView playerView;

   public VideoAttachmentView(Context context) {
      this(context, null);
   }

   public VideoAttachmentView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public VideoAttachmentView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      LayoutInflater.from(getContext()).inflate(R.layout.view_video_attachment, this, true);
      ButterKnife.inject(this);

      ((InjectingActivity) context).inject(this);
   }

   public void setup(Video video) {
      setVisibility(VISIBLE);
      videoThumbnailContainer.setVisibility(VISIBLE);
      this.video = video;
      ViewUtils.runTaskAfterMeasure(this, () -> {
         resizeVideoHolder();
         update();
      });
   }

   private void resizeVideoHolder() {
      int width = getWidth();
      int height = (int) (width / video.getAspectRatio());
      getLayoutParams().height = height;
      videoThumbnail.getLayoutParams().height = height;
      requestLayout();
   }

   public void hide() {
      setVisibility(GONE);
   }

   public void clearResources() {
      if (playerView != null) {
         videoThumbnailContainer.setVisibility(VISIBLE);
         playerView.stop();
         videoContainer.removeView(playerView);
         application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
         playerView = null;
      }
   }

   private void update() {
      videoThumbnail.setController(GraphicUtils.provideFrescoResizingController(Uri.parse(video.getThumbnail()),
            videoThumbnail.getController()));
   }

   public void onFocused() {
      // if playerView is not null - playback already initialized, player status from JWPlayer doesn't provide any
      // useful information
      if (playerView != null) {
         return;
      }
      showVideo();
   }

   @OnClick(R.id.playButton)
   public void onPlay() {
      showVideo();
   }

   private void showVideo() {
      //Stop video which playing now, on some neighbour cell
      if (videoPlayerHolder.getJwPlayerView() != null) {
         videoPlayerHolder.clearCurrent();
      }

      playProgressBar.setVisibility(VISIBLE);
      playButton.setVisibility(GONE);
      setupVideoPlayer();

      videoPlayerHolder.init(playerView, this);
      videoPlayerHolder.attachToContainer();
      videoPlayerHolder.play();
   }

   public FrameLayout getVideoContainer() {
      return videoContainer;
   }

   private void setupVideoPlayer() {
      playerView = new JWPlayerView(getContext(), new PlayerConfig.Builder()
            .mute(true)
            .playlist(preparePlaylist())
            .build());

      playerView.setSkin(SKIN_URL);
      playerView.addOnFirstFrameListener(i -> {
         videoThumbnailContainer.setVisibility(GONE);
         playButton.setVisibility(VISIBLE);
         playProgressBar.setVisibility(GONE);
      });
      playerView.addOnCompleteListener(() -> videoThumbnailContainer.setVisibility(VISIBLE));
      playerView.addOnErrorListener(new VideoPlayerEvents.OnErrorListenerV2() {
         @Override
         public void onError(ErrorEvent errorEvent) {
            clearResources();
         }
      });
      playerView.addOnSetupErrorListener(e -> clearResources());
      application.registerActivityLifecycleCallbacks(lifecycleCallbacks);
   }

   private List<PlaylistItem> preparePlaylist() {
      List<MediaSource> mediaSources = new ArrayList<>();
      mediaSources.add(new MediaSource.Builder().file(video.getSdUrl()).isdefault(true).label("SD").build());
      mediaSources.add(new MediaSource.Builder().file(video.getHdUrl()).label("HD").build());
      return Collections.singletonList(new PlaylistItem.Builder().sources(mediaSources).build());
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
}
