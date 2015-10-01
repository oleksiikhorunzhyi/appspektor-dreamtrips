package com.worldventures.dreamtrips.modules.feed.view.util;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.feed.bundle.CommentsBundle;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.view.custom.FeedActionPanelView;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;

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
                .data(new CommentsBundle(feedEntity, false))
                .move(Route.COMMENTS));

        actionView.setOnShareClickListener(feedItem -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(actionView.getContext());
            builder.title(R.string.action_share)
                    .items(R.array.share_dialog_items)
                    .itemsCallback((dialog, view, which, text) -> {
                        if (which == 0) {
                            fbShare();
                        } else {
                            twShare();
                        }
                    }).show();
        });
    }


    private void fbShare() {
      /*  activityRouter.openShareFacebook(bucketItem.getUrl(), null,
                String.format(context.getString(R.string.bucketlist_share),
                        bucketItem.getName())); todo*/
    }

    private void twShare() {
   /*     activityRouter.openShareTwitter(null, bucketItem.getUrl(),
                String.format(context.getString(R.string.bucketlist_share),
                        bucketItem.getName())); todo*/
    }

}
