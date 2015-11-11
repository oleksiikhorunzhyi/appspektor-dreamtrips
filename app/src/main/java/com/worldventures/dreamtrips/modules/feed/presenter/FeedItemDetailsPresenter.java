package com.worldventures.dreamtrips.modules.feed.presenter;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class FeedItemDetailsPresenter extends BaseCommentPresenter<FeedItemDetailsPresenter.View> {

    private FeedItem feedItem;
    //
    private UidItemDelegate uidItemDelegate;
    private WeakHandler handler = new WeakHandler();

    public FeedItemDetailsPresenter(FeedItem feedItem) {
        super(feedItem.getItem());
        this.feedItem = feedItem;
        uidItemDelegate = new UidItemDelegate(this);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setFeedItem(feedItem);
        loadFullEventInfo();
        //
        eventBus.post(new FeedItemAnalyticEvent(TrackingHelper.ATTRIBUTE_VIEW, feedItem.getItem().getUid(), feedItem.getType()));
    }

    @Override
    public void dropView() {
        super.dropView();
        handler.removeCallbacksAndMessages(null);
    }

    private void loadFullEventInfo() {
        doRequest(new GetFeedEntityQuery(feedEntity.getUid()), feedEntityHolder -> {
            FeedEntity freshItem = feedEntityHolder.getItem();
            freshItem.setFirstLikerName(feedEntity.getFirstLikerName());
            feedEntity = freshItem;
            feedItem.setItem(feedEntity);
            eventBus.post(new FeedEntityChangedEvent(feedEntity));
        });
    }

    public void onEvent(FeedEntityChangedEvent event) {
        if (event.getFeedEntity().equals(feedItem.getItem())) {
            view.updateFeedItem(feedItem);
        }
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

    private void itemLiked() {
        feedEntity.setLiked(!feedEntity.isLiked());
        int currentCount = feedEntity.getLikesCount();
        currentCount = feedEntity.isLiked() ? currentCount + 1 : currentCount - 1;
        feedEntity.setLikesCount(currentCount);
        eventBus.post(new FeedEntityChangedEvent(feedEntity));
        feedItem.setItem(feedEntity);
        view.updateFeedItem(feedItem);
    }

    public void onEvent(LoadFlagEvent event) {
        if (view.isVisibleOnScreen())
            uidItemDelegate.loadFlags(event.getFlaggableView());
    }

    public void onEvent(ItemFlaggedEvent event) {
        if (view.isVisibleOnScreen())
            uidItemDelegate.flagItem(new FlagData(event.getEntity().getUid(),
                    event.getFlagReasonId(), event.getNameOfReason()));
    }


    public interface View extends BaseCommentPresenter.View {
        void setFeedItem(FeedItem feedItem);

        void updateFeedItem(FeedItem feedItem);
    }
}
