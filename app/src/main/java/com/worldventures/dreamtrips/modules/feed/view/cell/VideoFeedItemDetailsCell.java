package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.service.ConfigurationInteractor;
import com.worldventures.dreamtrips.modules.feed.model.VideoFeedItem;
import com.worldventures.dreamtrips.modules.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.util.FeedCellListWidthProvider;
import com.worldventures.dreamtrips.modules.video.view.custom.VideoView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

@Layout(R.layout.adapter_item_feed_video_event)
public class VideoFeedItemDetailsCell extends FeedItemDetailsCell<VideoFeedItem, BaseFeedCell.FeedCellDelegate<VideoFeedItem>>
   implements Focusable {

   @InjectView(R.id.video_windowed_container) ViewGroup videoWindowedContainer;
   @InjectView(R.id.feed_share) ImageView share;

   private ViewGroup videoFullscreenContainer;
   @InjectView(R.id.videoAttachment) VideoView videoView;
   @Inject Activity activity;
   @Inject ConfigurationInteractor configurationInteractor;
   @Inject ActiveFeedRouteInteractor activeFeedRouteInteractor;
   private FeedCellListWidthProvider feedCellListWidthProvider;

   private Subscription configurationSubscription;
   private Route activeCellRoute;
   private boolean mute = false;

   public VideoFeedItemDetailsCell(View view) {
      super(view);
      feedCellListWidthProvider = new FeedCellListWidthProvider(view.getContext());
   }

   public void setMute(boolean mute) {
      this.mute = mute;
   }

   @Override
   public void afterInject() {
      super.afterInject();
      videoFullscreenContainer = ButterKnife.findById(activity, R.id.container_details_floating);
      activeCellRoute = getCurrentRoute();
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      videoView.setVideo(getModelObject().getItem(), true);
      videoView.setMute(mute);
      videoView.enableFullscreen(videoFullscreenContainer, videoWindowedContainer);
      if (configurationSubscription == null || configurationSubscription.isUnsubscribed()) {
         configurationSubscription = configurationInteractor
               .configurationActionPipe()
               .observeSuccess()
               .subscribe(configurationCommand -> videoView.resizeView(getCellListWidth()));
      }
   }

   private Route getCurrentRoute() {
      return activeFeedRouteInteractor.activeFeedRouteCommandActionPipe()
            .createObservableResult(ActiveFeedRouteCommand.fetch()).toBlocking().single().getResult();
   }

   private int getCellListWidth() {
      return feedCellListWidthProvider.getFeedCellWidth(activeCellRoute);
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

   @Override
   public void clearResources() {
      super.clearResources();
      videoView.clear();
      if (configurationSubscription != null && configurationSubscription.isUnsubscribed()) {
         configurationSubscription.unsubscribe();
      }
   }

   @Override
   public void onFocused() {
      videoView.play();
   }

   @Override
   public boolean canFocus() {
      return true;
   }
}
