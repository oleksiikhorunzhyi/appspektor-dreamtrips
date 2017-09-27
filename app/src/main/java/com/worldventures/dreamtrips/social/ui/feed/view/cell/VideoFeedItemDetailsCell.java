package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.VideoInfoInjector;
import com.worldventures.dreamtrips.social.ui.video.service.ConfigurationInteractor;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.service.ActiveFeedRouteInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ActiveFeedRouteCommand;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.util.FeedCellListWidthProvider;
import com.worldventures.dreamtrips.social.ui.video.view.custom.VideoView;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Subscription;

@Layout(R.layout.adapter_item_feed_video_event)
public class VideoFeedItemDetailsCell extends FeedItemDetailsCell<VideoFeedItem, BaseFeedCell.FeedCellDelegate<VideoFeedItem>>
   implements Focusable {

   @InjectView(R.id.feed_share) ImageView share;

   @InjectView(R.id.videoAttachment) VideoView videoView;
   @Inject Activity activity;
   @Inject ConfigurationInteractor configurationInteractor;
   @Inject ActiveFeedRouteInteractor activeFeedRouteInteractor;
   private FeedCellListWidthProvider feedCellListWidthProvider;
   private VideoInfoInjector videoInfoInjector = new VideoInfoInjector();

   private Subscription configurationSubscription;
   private Route activeCellRoute;
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
      videoInfoInjector.init(activity, itemView);
      activeCellRoute = getCurrentRoute();
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      videoInfoInjector.setVideo(videoView, getModelObject().getItem(), displayingInList);
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
