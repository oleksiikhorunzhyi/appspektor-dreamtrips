package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.navigation.wrapper.NavigationWrapper;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemShared;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.view.bundle.ShareBundle;
import com.worldventures.dreamtrips.modules.common.view.dialog.ShareDialog;
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

    Router router;
    EventBus eventBus;

    public FeedActionPanelViewActionHandler(Router router, EventBus eventBus) {
        this.router = router;
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
            new ShareDialog(actionView.getContext(), type -> {
                share(actionView.getContext(), feedItem, type);
            }).show();
        });

        actionView.setOnFlagClickListener(feedItem -> eventBus.post(new LoadFlagEvent(actionView)));
        actionView.setOnFlagDialogClickListener((feedItem, flagReasonId, reason) ->
                eventBus.post(new ItemFlaggedEvent(feedItem.getItem(), flagReasonId, reason)));
    }

    private void share(Context context, FeedItem feedItem, String shareType) {
        String imageUrl = null, shareUrl = null, text = null;
        switch (feedItem.getType()) {
            case PHOTO:
                Photo photo = (Photo) feedItem.getItem();
                shareUrl = photo.getFSImage().getUrl();
                text = photo.getFSShareText();

                break;

            case BUCKET_LIST_ITEM:
                BucketItem bucketItem = (BucketItem) feedItem.getItem();
                shareUrl = bucketItem.getUrl();
                text = String.format(context.getString(R.string.bucketlist_share),
                        bucketItem.getName());
                eventBus.post(new BucketItemShared());

                break;
        }

        ShareBundle data = new ShareBundle();
        data.setImageUrl(imageUrl);
        data.setShareUrl(shareUrl);
        data.setText(text == null ? "" : text);
        data.setShareType(shareType);
        router.moveTo(Route.SHARE, NavigationConfigBuilder.forActivity()
                .data(data)
                .build());
    }

}
