package com.worldventures.dreamtrips.modules.feed.presenter;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

import timber.log.Timber;

public class FeedDetailsPresenter<V extends FeedDetailsPresenter.View> extends BaseCommentPresenter<V> {

    private static final String TAG = FeedItemDetailsPresenter.class.getSimpleName();

    protected FeedItem feedItem;

    private WeakHandler handler = new WeakHandler();

    @Inject
    FeedEntityManager entityManager;

    public FeedDetailsPresenter(FeedItem feedItem) {
        super(feedItem.getItem());
        this.feedItem = feedItem;
    }

    @Override
    public void takeView(V view) {
        super.takeView(view);
        view.setFeedItem(feedItem);
        loadFullEventInfo();
    }

    @Override
    public void onInjected() {
        super.onInjected();
        entityManager.setRequestingPresenter(this);
    }

    @Override
    public void dropView() {
        super.dropView();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected boolean isNeedCheckCommentsWhenStart() {
        return false;
    }

    private void loadFullEventInfo() {
        doRequest(new GetFeedEntityQuery(feedEntity.getUid()), feedEntityHolder -> {
            updateFullEventInfo(feedEntityHolder);
        }, spiceException -> Timber.e(spiceException, TAG));
    }

    protected void updateFullEventInfo(FeedEntityHolder feedEntityHolder) {
        surviveNeedfulFields(feedEntity, feedEntityHolder);
        feedEntity = feedEntityHolder.getItem();
        feedEntity.setComments(null);
        feedItem.setItem(feedEntity);
        eventBus.post(new FeedEntityChangedEvent(feedEntity));
        checkCommentsAndLikesToLoad();
        view.updateFeedItem(feedItem);
    }

    private void surviveNeedfulFields(FeedEntity feedEntity, FeedEntityHolder feedEntityHolder) {
        feedEntity.setFirstLikerName(feedEntity.getFirstLikerName());
        if (feedEntityHolder.getType() == FeedEntityHolder.Type.TRIP) {
            TripModel freshItem = (TripModel) feedEntityHolder.getItem();
            TripModel oldItem = (TripModel) feedEntity;
            freshItem.setPrice(oldItem.getPrice());
            freshItem.setPriceAvailable(oldItem.isPriceAvailable());
            freshItem.setInBucketList(oldItem.isInBucketList());
        }
    }

    public void onEvent(FeedEntityChangedEvent event) {
        if (event.getFeedEntity().equals(feedItem.getItem())) {
            feedItem.setItem(event.getFeedEntity());
            feedEntity = event.getFeedEntity();
            view.updateFeedItem(feedItem);
        }
    }

    public void onEvent(LikesPressedEvent event) {
        if (view.isVisibleOnScreen()) {
            FeedEntity model = event.getModel();
            if (!model.isLiked()) {
                entityManager.like(model);
            } else {
                entityManager.unlike(model);
            }
        }
    }

    public void onEvent(EntityLikedEvent event) {
        feedEntity.syncLikeState(event.getFeedEntity());
        eventBus.post(new FeedEntityChangedEvent(feedEntity));
    }

    public void onEvent(FeedEntityCommentedEvent event) {
        if (event.getFeedEntity().equals(feedItem.getItem())) {
            feedItem.setItem(event.getFeedEntity());
            feedEntity = event.getFeedEntity();
            view.updateFeedItem(feedItem);
        }
    }

    public interface View extends BaseCommentPresenter.View {

        void setFeedItem(FeedItem feedItem);

        void updateFeedItem(FeedItem feedItem);
    }
}
