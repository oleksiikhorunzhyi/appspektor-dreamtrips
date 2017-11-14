package com.worldventures.dreamtrips.social.ui.feed.view.cell;

import android.content.Context;
import android.view.View;

import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.common.BucketUtility;
import com.worldventures.dreamtrips.social.ui.bucketlist.util.BucketItemInfoHelper;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.util.BucketItemViewInjector;
import com.worldventures.dreamtrips.social.ui.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.BucketFeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.BaseFeedCell;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.base.FeedItemDetailsCell;
import com.worldventures.dreamtrips.social.ui.feed.view.fragment.FeedEntityDetailsFragment;

import javax.inject.Inject;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_feed_bucket_event)
public class BucketFeedItemDetailsCell extends FeedItemDetailsCell<BucketFeedItem, BaseFeedCell.FeedCellDelegate<BucketFeedItem>> {

   @Inject SessionHolder appSessionHolder;
   @Inject BucketItemInfoHelper bucketItemInfoHelper;
   @ForActivity @Inject Context context;

   private BucketItemViewInjector bucketItemViewInjector;

   public BucketFeedItemDetailsCell(View view) {
      super(view);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      bucketItemViewInjector = new BucketItemViewInjector(itemView, context, appSessionHolder, bucketItemInfoHelper);
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
      router.moveTo(FeedEntityDetailsFragment.class, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create()
                  .visible(false)
                  .build())
            .data(new FeedEntityDetailsBundle.Builder().feedItem(getModelObject()).showAdditionalInfo(true).build())
            .build());
   }
}
