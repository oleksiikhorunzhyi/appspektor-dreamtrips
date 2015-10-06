package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.api.GetUsersLikedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedEntityDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.util.List;

import icepick.State;

public class FeedEntityDetailsPresenter extends Presenter<FeedEntityDetailsPresenter.View> {

    @State
    FeedItem feedModel;
    @State
    FeedEntity feedEntity;

    private UidItemDelegate uidItemDelegate;

    public FeedEntityDetailsPresenter(FeedEntityDetailsBundle args) {
        this.feedModel = args.getFeedItem();
        this.feedEntity = feedModel.getItem();
        uidItemDelegate = new UidItemDelegate(this);
    }


    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setHeader(feedModel);
        loadFullEventInfo();
        preloadUsersWhoLiked();

    }


    private void preloadUsersWhoLiked() {
        doRequest(new GetUsersLikedEntityQuery(feedEntity.getUid(), 1, 1), this::onUserLoaded,
                spiceException -> {
                });
    }

    private void onUserLoaded(List<User> users) {
        if (users != null && !users.isEmpty()) {
            feedModel.getItem().setFirstUserLikedItem(users.get(0).getFullName());
            view.updateHeader(feedModel);
            eventBus.post(new FeedEntityChangedEvent(feedModel.getItem()));
        }
    }

    private void loadFullEventInfo() {
        doRequest(new GetFeedEntityQuery(feedEntity.getUid()), feedObjectHolder -> {
            feedModel.setItem(feedObjectHolder.getItem());
            feedEntity = feedModel.getItem();
            view.updateHeader(feedModel);
            eventBus.post(new FeedEntityChangedEvent(feedModel.getItem()));
        });
    }

    public void onEvent(LikesPressedEvent event) {
        if (view.isVisibleOnScreen()) {
            FeedEntity model = event.getModel();
            DreamTripsRequest command = model.isLiked() ?
                    new UnlikeEntityCommand(model.getUid()) :
                    new LikeEntityCommand(model.getUid());
            doRequest(command, element -> itemLiked());
        }
    }

    public void onEvent(FeedEntityChangedEvent event) {
        if (event.getFeedEntity().equals(feedEntity)) {
            feedModel.setItem(event.getFeedEntity());
            feedEntity = feedModel.getItem();
            view.updateHeader(feedModel);
        }
    }

    public void onEvent(LoadFlagEvent event) {
        if (view.isVisibleOnScreen())
            uidItemDelegate.loadFlags(event.getFlaggableView());
    }

    public void onEvent(ItemFlaggedEvent event) {
        if (view.isVisibleOnScreen())
            uidItemDelegate.flagItem(event.getEntity().getUid(), event.getNameOfReason());
    }

    private void itemLiked() {
        feedEntity.setLiked(!feedEntity.isLiked());
        int currentCount = feedEntity.getLikesCount();
        currentCount = feedEntity.isLiked() ? currentCount + 1 : currentCount - 1;
        feedEntity.setLikesCount(currentCount);

        view.updateHeader(feedModel);
        eventBus.post(new FeedEntityChangedEvent(feedModel.getItem()));
        eventBus.post(new FeedEntityCommentedEvent(feedEntity));

        if (feedEntity.getLikesCount() == 1 && feedEntity.isLiked()) {
            preloadUsersWhoLiked();
        }
    }


    public interface View extends Presenter.View {

        void setHeader(FeedItem header);

        void updateHeader(FeedItem eventModel);

    }
}

