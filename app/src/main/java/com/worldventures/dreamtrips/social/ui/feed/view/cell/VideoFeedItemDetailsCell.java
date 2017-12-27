package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedCellListWidthProvider;
import com.worldventures.dreamtrips.social.ui.video.view.custom.DTVideoConfig;
import com.worldventures.dreamtrips.social.ui.video.view.custom.DTVideoViewImpl;
import com.worldventures.dreamtrips.social.ui.video.view.custom.VideoPlayerHolder;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_feed_video_event)
public class VideoFeedItemDetailsCell extends FeedItemDetailsCell<VideoFeedItem, BaseFeedCell.FeedCellDelegate<VideoFeedItem>>
      implements Focusable {

   @InjectView(R.id.feed_share) ImageView share;

   @InjectView(R.id.videoAttachment) DTVideoViewImpl dtVideoView;
   @Inject ActiveFeedRouteInteractor activeFeedRouteInteractor;
   @Inject VideoPlayerHolder videoPlayerHolder;

   private FeedCellListWidthProvider feedCellListWidthProvider;

   private FeedCellListWidthProvider.FeedType activeFeedType;
   private boolean displayingInList;

   public VideoFeedItemDetailsCell(View view) {
      super(view);
      feedCellListWidthProvider = new FeedCellListWidthProvider(view.getContext());
   }

   public void setDisplayingInList(boolean displayingInList) {
      this.displayingInList = displayingInList;
   }

   @Override
   public void afterInject() {
      super.afterInject();
      activeFeedType = getCurrentRoute();
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      updateVideoHeight(getModelObject().getItem());
      dtVideoView.setThumbnail(getModelObject().getItem().getThumbnail());
      dtVideoView.setThumbnailAction(this::playVideoIfNeeded);
      if (!displayingInList && playerExistsAndCurrentItemIsSame(getModelObject().getItem())) {
         if (videoPlayerHolder.inFullscreen()) {
            switchFromFullscreen();
         } else {
            reattachVideoView();
            dtVideoView.pauseVideo();
         }
      }
   }

   private FeedCellListWidthProvider.FeedType getCurrentRoute() {
      return activeFeedRouteInteractor.activeFeedRouteCommandActionPipe()
            .createObservableResult(ActiveFeedRouteCommand.fetch()).toBlocking().single().getResult();
   }

   private int getCellListWidth() {
      return feedCellListWidthProvider.getFeedCellWidth(activeFeedType);
   }

   @Override
   protected void onMore() {
      showMoreDialog(R.menu.menu_feed_entity_delete, R.string.video_delete, R.string.video_delete_caption);
   }

   @Override
   protected void onDelete() {
      super.onDelete();
      cellDelegate.onDeleteVideo(getModelObject().getItem());
   }

   private void updateVideoHeight(Video video) {
      int height = (int) (getCellListWidth() / video.getAspectRatio());
      ViewGroup.LayoutParams params = dtVideoView.getLayoutParams();
      params.height = height;
      dtVideoView.setLayoutParams(params);
   }

   @Override
   public void onFocused() {
      playVideoIfNeeded();
   }

   private void playVideoIfNeeded() {
      Video video = getModelObject().getItem();
      if (playerExistsAndCurrentItemIsSame(video)) {
         if (videoPlayerHolder.inFullscreen()) {
            switchFromFullscreen();
         } else {
            reattachVideoView();
         }
      } else {
         dtVideoView.playVideo(new DTVideoConfig(video.getUid(), displayingInList, video.getQualities(), 0));
      }
   }

   private void switchFromFullscreen() {
      videoPlayerHolder.switchFromFullscreen(dtVideoView, displayingInList);
   }

   private boolean playerExistsAndCurrentItemIsSame(Video video) {
      return videoPlayerHolder.getCurrentVideoConfig() != null
            && video.getUid().equals(videoPlayerHolder.getCurrentVideoConfig().getUid());
   }

   private void reattachVideoView() {
      videoPlayerHolder.reattachVideoView(dtVideoView, displayingInList
            || videoPlayerHolder.getCurrentVideoConfig().getMute());
   }

   @Override
   public void onUnfocused() {
      dtVideoView.pauseVideo();
      dtVideoView.detachPlayer();
      dtVideoView.showThumbnail();
   }

   @Override
   public void clearResources() {
      super.clearResources();
      dtVideoView.detachPlayer();
      dtVideoView.showThumbnail();
   }

   @Override
   public boolean canFocus() {
      return true;
   }
}
