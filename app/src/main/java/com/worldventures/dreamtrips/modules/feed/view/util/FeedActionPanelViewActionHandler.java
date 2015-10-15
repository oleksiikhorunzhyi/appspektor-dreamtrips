package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.view.activity.ShareFragment;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
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

    public void init(FeedActionPanelView actionView) {


        actionView.setOnLikeIconClickListener(feedItem -> eventBus.post(new LikesPressedEvent(feedItem.getItem())));

        actionView.setOnLikersClickListener(feedItem -> NavigationBuilder.create()
                .with(activityRouter)
                .data(new UsersLikedEntityBundle(feedItem.getItem().getUid()))
                .move(Route.USERS_LIKED_CONTENT));

        actionView.setOnCommentIconClickListener(feedEntity -> NavigationBuilder.create()
                .with(activityRouter)
                .data(new CommentsBundle(feedEntity.getItem(), false))
                .move(Route.COMMENTS));

        actionView.setOnShareClickListener(feedItem -> {
            new ShareDialog(actionView.getContext(), type -> {
                share(actionView.getContext(), feedItem, type);
            }).show();
        });

        actionView.setOnFlagClickListener(feedItem -> eventBus.post(new LoadFlagEvent(actionView)));
        actionView.setOnFlagDialogClickListener((feedItem, reason, desc) ->
                eventBus.post(new ItemFlaggedEvent(feedItem.getItem(), reason + ". " + desc)));
    }

    private void share(Context context, FeedItem feedItem, String shareType) {
        String imageUrl = null, shareUrl = null, text = null;
        switch (feedItem.getType()) {
            case PHOTO:
                Photo photo = (Photo) feedItem.getItem();
                shareUrl = photo.getFSImage().getUrl();
                text = photo.getFsShareText();

                break;

            case BUCKET_LIST_ITEM:
                BucketItem bucketItem = (BucketItem) feedItem.getItem();
                imageUrl = bucketItem.getUrl();
                text = String.format(context.getString(R.string.bucketlist_share),
                        bucketItem.getName());

                break;
        }

        if (shareType.equals(ShareFragment.FB)) {
            activityRouter.openShareFacebook(imageUrl, shareUrl, text);
        } else {
            activityRouter.openShareTwitter(imageUrl, shareUrl, text);
        }
    }
}
