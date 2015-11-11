package com.worldventures.dreamtrips.modules.feed.view.util;


import android.os.Parcelable;
import android.util.Pair;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.BucketDetailsRouteCreator;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.PhotoBundle;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class FeedEntityContentFragmentFactory {

    private final RouteCreator bucketRouteCreator;

    public FeedEntityContentFragmentFactory(SessionHolder<UserSession> sessionHolder) {
        this.bucketRouteCreator = new BucketDetailsRouteCreator(sessionHolder);
    }

    public Pair<Route, Parcelable> create(FeedEntityHolder holder) {
        FeedEntityHolder.Type type = holder.getType();

        Route route = null;
        Parcelable args = null;
        switch (type) {
            case TRIP:
            case UNDEFINED:
                //now is not used.
                break;
            case PHOTO:
                route = Route.DETAILS_PHOTO;
                args = new PhotoBundle((Photo) holder.getItem());
                break;
            case BUCKET_LIST_ITEM:
                User user = holder.getItem().getOwner();
                int userId = user != null ? user.getId() : 0;
                route = bucketRouteCreator.createRoute(userId);
                BucketBundle bucketBundle = new BucketBundle();
                BucketItem item = (BucketItem) holder.getItem();
                bucketBundle.setType(item.getType());
                bucketBundle.setBucketItemUid(item.getUid());
                bucketBundle.setOwnerId(userId);
                args = bucketBundle;
                break;
            case POST:
                route = Route.DETAILS_TEXTUAL_POST;
                args = new PostBundle((TextualPost) holder.getItem());
                break;
        }
        return new Pair<>(route, args);
    }
}
