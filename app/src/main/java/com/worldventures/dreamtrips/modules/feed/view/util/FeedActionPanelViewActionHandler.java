package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemShared;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.feed.event.CommentIconClickedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import de.greenrobot.event.EventBus;

public class FeedActionPanelViewActionHandler {

    ActivityRouter activityRouter;
    EventBus eventBus;

    public FeedActionPanelViewActionHandler(ActivityRouter activityRouter, EventBus eventBus) {
        this.activityRouter = activityRouter;
        this.eventBus = eventBus;
    }

    public void init(FeedActionPanelView actionView, NavigationWrapper navigationWrapper) {
        actionView.setOnLikeIconClickListener(feedItem -> {
            eventBus.post(new LikesPressedEvent(feedItem.getItem()));

            String id = feedItem.getItem().getUid();
            FeedEntityHolder.Type type = feedItem.getType();
            if (type != FeedEntityHolder.Type.UNDEFINED) {
                eventBus.post(new FeedItemAnalyticEvent(TrackingHelper.ATTRIBUTE_LIKE, id, type));
            }
        });

        actionView.setOnLikersClickListener(feedItem -> {
            navigationWrapper.navigate(Route.USERS_LIKED_CONTENT, new UsersLikedEntityBundle(feedItem.getItem().getUid()));
        });

        actionView.setOnCommentIconClickListener(feedItem -> eventBus.post(new CommentIconClickedEvent(feedItem)));

        actionView.setOnShareClickListener(feedItem -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(actionView.getContext());
            builder.title(R.string.action_share)
                    .items(R.array.share_dialog_items)
                    .itemsCallback((dialog, view, which, text) -> {
                        String shareType;
                        if (which == 0) {
                            shareType = ShareFragment.FB;
                        } else {
                            shareType = ShareFragment.TW;
                        }

                        share(feedItem, actionView.getContext(), shareType);
                    }).show();
        });

        actionView.setOnFlagClickListener(feedItem -> eventBus.post(new LoadFlagEvent(actionView)));
        actionView.setOnFlagDialogClickListener((feedItem, flagReasonId, reason) ->
                eventBus.post(new ItemFlaggedEvent(feedItem.getItem(), flagReasonId, reason)));
    }

    private void share(FeedItem feedItem, Context context, String shareType) {
        String imageUrl = null, shareUrl = null, text = null;
        switch (feedItem.getType()) {
            case PHOTO:
                Photo photo = (Photo) feedItem.getItem();
                shareUrl = photo.getFSImage().getUrl();
                text = photo.getFsShareText();

                break;

            case BUCKET_LIST_ITEM:
                BucketItem bucketItem = (BucketItem) feedItem.getItem();
                shareUrl = bucketItem.getUrl();
                text = String.format(context.getString(R.string.bucketlist_share),
                        bucketItem.getName());
                eventBus.post(new BucketItemShared());

                break;
        }

        if (shareType.equals(ShareFragment.FB)) {
            activityRouter.openShareFacebook(imageUrl, shareUrl, text);
        } else {
            activityRouter.openShareTwitter(imageUrl, shareUrl, text);
        }
    }

}
