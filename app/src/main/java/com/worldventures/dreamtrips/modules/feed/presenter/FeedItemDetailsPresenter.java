package com.worldventures.dreamtrips.modules.feed.presenter;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedEntityQuery;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import javax.inject.Inject;

import timber.log.Timber;

public class FeedItemDetailsPresenter extends BaseCommentPresenter<FeedItemDetailsPresenter.View> {

    private static final String TAG = FeedItemDetailsPresenter.class.getSimpleName();

    private FeedItem feedItem;
    //
    private UidItemDelegate uidItemDelegate;
    private WeakHandler handler = new WeakHandler();

    @Inject
    FeedEntityManager entityManager;

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
    public void onInjected() {
        super.onInjected();
        entityManager.setDreamSpiceManager(dreamSpiceManager);
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
            //
            if (view.isTabletLandscape())
                view.showAdditionalInfo(feedEntity.getOwner());
        }, spiceException -> Timber.e(spiceException, TAG));
    }

    public void onEvent(FeedEntityChangedEvent event) {
        if (event.getFeedEntity().equals(feedItem.getItem())) {
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

    public void onEvent(EntityLikedEvent event){
        feedEntity.syncLikeState(event.getFeedEntity());
        eventBus.post(new FeedEntityChangedEvent(feedEntity));

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

        void showAdditionalInfo(User user);
    }
}
