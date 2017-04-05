package com.worldventures.dreamtrips.modules.common.view.jwplayer;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.configuration.PlayerConfig;
import com.longtailvideo.jwplayer.media.playlists.MediaSource;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;
import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.GraphicUtils;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;
import com.worldventures.dreamtrips.modules.feed.view.cell.util.PickerVideoDurationFormatter;

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

   @Inject PickerVideoDurationFormatter pickerVideoDurationFormatter;
   @Inject VideoPlayerHolder videoPlayerHolder;

   @InjectView(R.id.length) TextView duration;
   @InjectView(R.id.videoThumbnail) SimpleDraweeView videoThumbnail;

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
         playerView.stop();
         playerView.setVisibility(GONE);
         removeView(playerView);
         playerView = null;
      }
   }

   private void update() {
      duration.setText(pickerVideoDurationFormatter.getFormattedDuration(video.getDuration()));
      videoThumbnail.setController(GraphicUtils.provideFrescoResizingController(Uri.parse(video.getThumbnail()),
            videoThumbnail.getController()));
   }

   @OnClick(R.id.play)
   void onPlay() {
      showVideo();
   }

   private void showVideo() {
      playerView = new JWPlayerView(getContext(), new PlayerConfig.Builder()
            .mute(true)
            .playlist(preparePlaylist())
            .build());

      playerView.setSkin(SKIN_URL);

      videoPlayerHolder.init(playerView, this);
      videoPlayerHolder.attachToContainer();
      videoPlayerHolder.play();
   }

   private List<PlaylistItem> preparePlaylist() {
      List<MediaSource> mediaSources = new ArrayList<>();
      mediaSources.add(new MediaSource.Builder().file(video.getSdUrl()).isdefault(true).label("SD").build());
      mediaSources.add(new MediaSource.Builder().file(video.getHdUrl()).label("HD").build());
      return Collections.singletonList(new PlaylistItem.Builder().sources(mediaSources).build());
   }
}
