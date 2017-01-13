package com.worldventures.dreamtrips.modules.feed.view.cell;

import android.content.Context;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.common.BucketUtility;
import com.worldventures.dreamtrips.modules.bucketlist.view.util.BucketItemViewInjector;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.base.FeedItemDetailsCell;

import javax.inject.Inject;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_feed_bucket_event)
public class BucketFeedItemDetailsCell extends FeedItemDetailsCell<BucketFeedItem, BaseFeedCell.FeedCellDelegate<BucketFeedItem>> {

   @Inject SessionHolder<UserSession> appSessionHolder;
   @ForActivity @Inject Context context;

   private BucketItemViewInjector bucketItemViewInjector;

   public BucketFeedItemDetailsCell(View view) {
      super(view);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      bucketItemViewInjector = new BucketItemViewInjector(itemView, context, appSessionHolder);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      bucketItemViewInjector.processBucketItem(getModelObject().getItem());
   }

   @OnClick(R.id.translate)
   public void translate() {
      if (getModelObject().getItem().isTranslated()) {
         cellDelegate.onShowOriginal(getModelObject().getItem());
      } else {
         bucketItemViewInjector.translatePressed();
         cellDelegate.onTranslateItem(getModelObject().getItem());
      }
   }

   @Override
   protected void onMore() {
      showMoreDialog(R.menu.menu_feed_entity_edit, R.string.bucket_delete, R.string.bucket_delete_caption);
   }

   @Override
   protected void onDelete() {
      super.onDelete();
      cellDelegate.onDeleteBucketItem(getModelObject().getItem());
   }

   @Override
   protected void onEdit() {
      super.onEdit();
      BucketItem bucketItem = getModelObject().getItem();
      cellDelegate.onEditBucketItem(bucketItem, BucketUtility.typeFromItem(bucketItem));
   }

   @OnClick(R.id.bucket_main)
   void openBucketEntityDetails() {
      router.moveTo(Route.FEED_ENTITY_DETAILS, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create()
                  .visible(false)
                  .build())
            .data(new FeedEntityDetailsBundle.Builder().feedItem(getModelObject()).showAdditionalInfo(true).build())
            .build());
   }
}
