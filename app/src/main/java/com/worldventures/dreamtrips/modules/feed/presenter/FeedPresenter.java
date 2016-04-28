package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountFeedQuery;
import com.worldventures.dreamtrips.modules.feed.api.PhotoGalleryRequest;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import icepick.State;
import rx.Observable;
import rx.Subscription;

public class FeedPresenter extends BaseFeedPresenter<FeedPresenter.View> {
    private static final int SUGGESTION_ITEM_CHUNK = 15;

    @Inject
    SnappyRepository db;
    //
    @Inject
    UnreadConversationObservable observable;

    Subscription unreadConversationSubscription;
    //
    Circle filterCircle;
    @State
    int unreadConversationCount;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    private SuggestedPhotoCellPresenterHelper presenterHelper;

    @Override
    public void saveInstanceState(Bundle outState) {
        super.saveInstanceState(outState);

        if (presenterHelper != null) {
            presenterHelper.saveInstanceState(outState);
        }
    }

    private UidItemDelegate uidItemDelegate;

    public FeedPresenter() {
        uidItemDelegate = new UidItemDelegate(this);
    }

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        filterCircle = db.getFilterCircle();
        if (filterCircle == null) filterCircle = createDefaultFilterCircle();
    }

    @Override
    public void onStart() {
        super.onStart();
        unreadConversationSubscription = observable.subscribe(count -> {
            unreadConversationCount = count;
            view.setUnreadConversationCount(count);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFeed();
    }

    @Override
    protected void refreshFeedSucceed(List<ParentFeedItem> freshItems) {
        loading = false;
        noMoreFeeds = freshItems == null || freshItems.size() == 0;
        view.finishLoading();
        feedItems.clear();
        feedItems.addAll(Queryable.from(freshItems)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());
        //
        doRequest(new PhotoGalleryRequest(context), photos -> {
            if (isHasNewPhotos(photos)) {
                view.refreshFeedItems(feedItems, Queryable.from(photos).take(SUGGESTION_ITEM_CHUNK).toList(), !noMoreFeeds);
            } else {
                view.refreshFeedItems(feedItems, !noMoreFeeds);
            }
        }, error -> view.refreshFeedItems(feedItems, !noMoreFeeds));
    }

    public boolean isHasNewPhotos(List<PhotoGalleryModel> photos) {
        return photos != null && !photos.isEmpty() && photos.get(0).getDateTaken() > db.getLastSuggestedPhotosSyncTime();
    }

    public void removeSuggestedPhotos() {
        presenterHelper.reset();
        view.refreshFeedItems(feedItems, !noMoreFeeds);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (unreadConversationSubscription != null && !unreadConversationSubscription.isUnsubscribed()) {
            unreadConversationSubscription.unsubscribe();
        }
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getRefreshFeedRequest(Date date) {
        return new GetAccountFeedQuery(filterCircle.getId());
    }

    @Override
    protected DreamTripsRequest<ArrayList<ParentFeedItem>> getNextPageFeedRequest(Date date) {
        return new GetAccountFeedQuery(date, filterCircle.getId());
    }

    public List<Circle> getFilterCircles() {
        List<Circle> circles = db.getCircles();
        Collections.sort(circles);
        circles.add(0, createDefaultFilterCircle());
        return circles;
    }

    public void onEvent(LoadFlagEvent event) {
        if (view.isVisibleOnScreen())
            uidItemDelegate.loadFlags(event.getFlaggableView());
    }

    public void onEvent(ItemFlaggedEvent event) {
        if (view.isVisibleOnScreen())
            uidItemDelegate.flagItem(new FlagData(event.getEntity().getUid(),
                    event.getFlagReasonId(), event.getNameOfReason()), view);
    }

    private Circle createDefaultFilterCircle() {
        return Circle.all(context.getString(R.string.all));
    }

    public Circle getAppliedFilterCircle() {
        return filterCircle;
    }

    public void applyFilter(Circle selectedCircle) {
        filterCircle = selectedCircle;
        db.saveFilterCircle(selectedCircle);
        onRefresh();
    }

    public void onUnreadConversationsClick() {
        MessengerActivity.startMessenger(activityRouter.getContext());
    }

    public void onEventMainThread(HeaderCountChangedEvent event) {
        view.setRequestsCount(getFriendsRequestsCount());
    }

    public void onEvent(FeedItemAnalyticEvent event) {
        TrackingHelper.sendActionItemFeed(event.getActionAttribute(), event.getEntityId(), event.getType());
    }

    public int getFriendsRequestsCount() {
        return db.getFriendsRequestsCount();
    }

    public int getUnreadConversationCount() {
        return unreadConversationCount;
    }

    public interface View extends BaseFeedPresenter.View, UidItemDelegate.View {

        void setRequestsCount(int count);

        void setUnreadConversationCount(int count);

        void refreshFeedItems(List<FeedItem> feedItems, List<PhotoGalleryModel> suggestedPhotos, boolean needLoader);
    }

    //new
    public void takeSuggestionView(SuggestedPhotoCellPresenterHelper.View view, SuggestedPhotoCellPresenterHelper.OutViewBinder binder,
                                   Bundle bundle, Observable<Void> notificationObservable) {
        injectorProvider.get().inject(presenterHelper = new SuggestedPhotoCellPresenterHelper());

        presenterHelper.takeView(view, binder, bundle);
        presenterHelper.subscribeNewPhotoNotifications(notificationObservable);
    }

    public void preloadSuggestionChunk(@NonNull PhotoGalleryModel model) {
        presenterHelper.preloadSuggestionPhotos(model);
    }

    public void syncSuggestionViewState() {
        presenterHelper.sync();
    }

    public void openProfile() {
        presenterHelper.openProfile();
    }

    public void selectPhoto(@NonNull PhotoGalleryModel model) {
        presenterHelper.selectPhoto(model);
    }

    public List<PhotoGalleryModel> getSelectedSuggestionPhotos() {
        return presenterHelper.selectedPhotos();
    }

    public long lastSyncTimestamp() {
        return presenterHelper.lastSyncTime();
    }
}