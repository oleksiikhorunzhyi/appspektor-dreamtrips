package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
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
import com.worldventures.dreamtrips.modules.feed.view.fragment.FeedEntityDetailsFragment;

import icepick.State;

public class FeedEntityDetailsPresenter extends Presenter<FeedEntityDetailsPresenter.View> {

    @State
    FeedItem feedItem;
    @State
    FeedEntity feedEntity;

    private UidItemDelegate uidItemDelegate;

    public FeedEntityDetailsPresenter(FeedEntityDetailsBundle args) {
        this.feedItem = args.getFeedItem();
        this.feedEntity = feedItem.getItem();
        uidItemDelegate = new UidItemDelegate(this);
    }


    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (feedEntity.getUser() != null) {
            view.setHeader(feedItem);
            view.setContent(feedItem);
        }
        view.setSocial(feedItem);
        loadFullEventInfo();
    }


    private void loadFullEventInfo() {
        doRequest(new GetFeedEntityQuery(feedEntity.getUid()), feedEntityHolder -> {
            feedItem.setItem(feedEntityHolder.getItem());
            feedEntity = feedItem.getItem();
            view.setHeader(feedItem);
            view.updateContent(feedItem);
            view.setSocial(feedItem);
            eventBus.post(new FeedEntityChangedEvent(feedEntity));
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
        onEntityChanged(event.getFeedEntity());
    }

    public void onEvent(FeedEntityCommentedEvent event) {
        onEntityChanged(event.getFeedEntity());
    }

    private void onEntityChanged(FeedEntity feedEntity) {
        if (feedEntity.equals(this.feedEntity)) {
            feedEntity.setFirstUserLikedItem(feedItem.getItem().getFirstUserLikedItem());
            feedItem.setItem(feedEntity);
            this.feedEntity = feedItem.getItem();
            view.setHeader(feedItem);
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

        view.setHeader(feedItem);
        eventBus.post(new FeedEntityChangedEvent(feedItem.getItem()));
        eventBus.post(new FeedEntityCommentedEvent(feedEntity));
    }


    public interface View extends Presenter.View {

        void setHeader(FeedItem feedItem);

        void setContent(FeedItem feedItem);

        void setSocial(FeedItem feedItem);

        void updateContent(FeedItem feedItem);
    }
}

