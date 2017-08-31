package com.worldventures.dreamtrips.modules.feed.view.cell.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.video.Video;
import com.worldventures.dreamtrips.modules.video.utils.mute_strategy.FullscreenMuteStrategy;
import com.worldventures.dreamtrips.modules.video.view.custom.VideoView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class VideoInfoInjector {

   private ViewGroup videoFullscreenContainer;
   @InjectView(R.id.video_windowed_container) ViewGroup videoWindowedContainer;

   public void init(Activity activity, View cellView) {
      videoFullscreenContainer = ButterKnife.findById(activity, R.id.container_details_floating);
      videoWindowedContainer = ButterKnife.findById(cellView, R.id.video_windowed_container);
   }

   public void setVideo(VideoView videoView, Video video, boolean displayingInList) {
      videoView.setVideo(video, true);
      videoView.setMute(displayingInList);
      FullscreenMuteStrategy fullscreenMuteStrategy;
      if (displayingInList) {
         fullscreenMuteStrategy = FullscreenMuteStrategy.MUTE_IN_WINDOWED_SOUND_IN_FULLSCREEN;
      } else {
         fullscreenMuteStrategy = FullscreenMuteStrategy.KEEP_CURRENT_VALUE;
      }
      videoView.enableFullscreen(videoFullscreenContainer, videoWindowedContainer, fullscreenMuteStrategy);
   }
}
