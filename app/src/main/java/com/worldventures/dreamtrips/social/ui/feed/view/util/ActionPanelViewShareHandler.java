package com.worldventures.dreamtrips.social.ui.feed.view.util;

import android.content.Context;

import com.worldventures.core.model.ShareType;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.modules.common.view.dialog.PhotosShareDialog;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.analytics.BucketItemAction;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.social.ui.share.bundle.ShareBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import rx.functions.Action1;

public class ActionPanelViewShareHandler {

   private Router router;
   private Action1<String> downloadPhotoAction;
   private AnalyticsInteractor analyticsInteractor;

   public ActionPanelViewShareHandler(Router router, AnalyticsInteractor analyticsInteractor) {
      this.router = router;
      this.analyticsInteractor = analyticsInteractor;
   }

   public void init(FeedActionPanelView actionView, Action1<String> downloadPhotoAction) {
      actionView.setOnShareClickListener(feedItem -> {
         ShareDialog.ShareDialogCallback callback = type -> onShare(actionView.getContext(), feedItem, type);
         if (feedItem.getType() == FeedEntityHolder.Type.PHOTO) {
            new PhotosShareDialog(actionView.getContext(), callback).show();
         } else {
            new ShareDialog(actionView.getContext(), callback).show();
         }
      });
      this.downloadPhotoAction = downloadPhotoAction;
   }

   private void onShare(Context context, FeedItem feedItem, String shareType) {
      if (shareType.equals(ShareType.EXTERNAL_STORAGE)) {
         downloadPhoto(feedItem);
      } else {
         share(context, feedItem, shareType);
      }
   }

   private void downloadPhoto(FeedItem feedItem) {
      String url = ((Photo) feedItem.getItem()).getImagePath();
      downloadPhotoAction.call(url);
   }

   private void share(Context context, FeedItem feedItem, String shareType) {
      String imageUrl = null, shareUrl = null, text = null;
      switch (feedItem.getType()) {
         case PHOTO:
            Photo photo = (Photo) feedItem.getItem();
            shareUrl = photo.getImagePath();
            text = photo.getTitle();
            break;
         case BUCKET_LIST_ITEM:
            BucketItem bucketItem = (BucketItem) feedItem.getItem();
            shareUrl = bucketItem.getUrl();
            text = context.getString(R.string.bucketlist_share, bucketItem.getName());
            analyticsInteractor.analyticsActionPipe().send(BucketItemAction.share(bucketItem.getUid()));
            break;
         default:
            break;
      }

      ShareBundle data = new ShareBundle();
      data.setImageUrl(imageUrl);
      data.setShareUrl(shareUrl);
      data.setText(text == null ? "" : text);
      data.setShareType(shareType);
      router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity().data(data).build());
   }
}
