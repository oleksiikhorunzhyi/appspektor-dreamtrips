package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.ui.activity.MessengerActivity;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.DeleteItemHttpAction;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.api.PhotoGalleryRequest;
import com.worldventures.dreamtrips.modules.feed.event.DeleteBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.DeletePostEvent;
import com.worldventures.dreamtrips.modules.feed.event.DownloadPhotoEvent;
import com.worldventures.dreamtrips.modules.feed.event.EditBucketEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityCommentedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityDeletedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedItemAddedEvent;
import com.worldventures.dreamtrips.modules.feed.event.ItemFlaggedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LoadFlagEvent;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.GetAccountFeedQueryCommand;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DownloadImageCommand;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class FeedPresenter extends Presenter<FeedPresenter.View> {

    private static final int SUGGESTION_ITEM_CHUNK = 15;

    @Inject
    protected FeedEntityManager entityManager;
    @Inject
    SnappyRepository db;
    @Inject
    MediaPickerManager mediaPickerManager;
    @Inject
    DrawableUtil drawableUtil;
    @Inject
    UnreadConversationObservable unreadConversationObservable;
    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @Inject
    protected BucketInteractor bucketInteractor;
    @Inject
    protected FeedInteractor feedInteractor;

    Circle filterCircle;

    private UidItemDelegate uidItemDelegate;

    @State
    protected ArrayList<FeedItem> feedItems;
    @State
    int unreadConversationCount;

    private SuggestedPhotoCellPresenterHelper presenterHelper;

    public FeedPresenter() {
        uidItemDelegate = new UidItemDelegate(this);
    }

    @Override
    public void onInjected() {
        super.onInjected();
        entityManager.setRequestingPresenter(this);
    }

    @Override
    public void saveInstanceState(Bundle outState) {
        super.saveInstanceState(outState);
        if (presenterHelper != null) {
            presenterHelper.saveInstanceState(outState);
        }
    }

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        if (savedState == null) feedItems = new ArrayList<>();
        filterCircle = db.getFilterCircle();
        if (filterCircle == null) filterCircle = createDefaultFilterCircle();
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        if (feedItems.size() != 0) {
            view.refreshFeedItems(feedItems);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeRefreshFeeds();
        subscribeLoadNextFeeds();
        subscribeUnreadConversation();
    }

    //region refresh feeds (temp refactoring region)
    private void subscribeRefreshFeeds() {
        view.bind(feedInteractor.getRefreshAccountFeedQueryPipe().observe())
                .compose(new ActionStateToActionTransformer<>())
                .map(GetAccountFeedQueryCommand.Refresh::getResult)
                .compose(new IoToMainComposer<>())
                .subscribe(parentFeedItems -> refreshFeedSucceed(parentFeedItems),
                        throwable -> {
                            refreshFeedError();
                            Timber.e(throwable, "");
                        });
    }

    private void refreshFeedSucceed(List<ParentFeedItem> freshItems) {
        boolean noMoreFeeds = freshItems == null || freshItems.size() == 0;
        view.updateLoadingStatus(false, noMoreFeeds);
        //
        view.finishLoading();
        feedItems.clear();
        feedItems.addAll(Queryable.from(freshItems)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());
        //
        doRequest(new PhotoGalleryRequest(context), photos -> {
            if (isHasNewPhotos(photos)) {
                view.refreshFeedItems(feedItems, Queryable.from(photos).take(SUGGESTION_ITEM_CHUNK).toList());
            } else {
                view.refreshFeedItems(feedItems);
            }
        }, error -> view.refreshFeedItems(feedItems));
    }

    private void refreshFeedError() {
        view.updateLoadingStatus(false, false);
        view.finishLoading();
        view.refreshFeedItems(feedItems);
    }

    public void refreshFeed() {
        view.startLoading();
        feedInteractor.getRefreshAccountFeedQueryPipe().send(new GetAccountFeedQueryCommand.Refresh(filterCircle.getId()));
    }
    //endregion

    //region load next feeds (temp refactoring region)
    private void subscribeLoadNextFeeds() {
        view.bind(feedInteractor.getLoadNextAccountFeedQueryPipe().observe())
                .compose(new ActionStateToActionTransformer<>())
                .map(GetAccountFeedQueryCommand.LoadNext::getResult)
                .compose(new IoToMainComposer<>())
                .subscribe(parentFeedItems -> addFeedItems(parentFeedItems),
                        throwable -> {
                            loadMoreItemsError();
                            Timber.e(throwable, "");
                        });
    }

    private void addFeedItems(List<ParentFeedItem> olderItems) {
        boolean noMoreFeeds = olderItems == null || olderItems.size() == 0;
        view.updateLoadingStatus(false, noMoreFeeds);
        //
        feedItems.addAll(Queryable.from(olderItems)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());
        view.refreshFeedItems(feedItems);
    }

    private void loadMoreItemsError() {
        view.updateLoadingStatus(false, false);
        addFeedItems(new ArrayList<>());
    }

    public void loadNext() {
        if (feedItems.size() > 0) {
            feedInteractor.getLoadNextAccountFeedQueryPipe().send(new GetAccountFeedQueryCommand.LoadNext(
                    filterCircle.getId(),
                    feedItems.get(feedItems.size() - 1).getCreatedAt()));
        }
    }
    //endregion

    private void subscribeUnreadConversation() {
        view.bindUntilStop(unreadConversationObservable.getObservable())
                .subscribe(count -> {
                    unreadConversationCount = count;
                    view.setUnreadConversationCount(count);
                }, throwable -> Timber.w("Can't get unread conversation count"));
    }

    public boolean isHasNewPhotos(List<PhotoGalleryModel> photos) {
        return photos != null && !photos.isEmpty() && photos.get(0).getDateTaken() > db.getLastSuggestedPhotosSyncTime();
    }

    public void removeSuggestedPhotos() {
        presenterHelper.reset();
        view.refreshFeedItems(feedItems);
    }

    public List<Circle> getFilterCircles() {
        List<Circle> circles = db.getCircles();
        Collections.sort(circles);
        circles.add(0, createDefaultFilterCircle());
        return circles;
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
        refreshFeed();
    }

    public void onUnreadConversationsClick() {
        MessengerActivity.startMessenger(activityRouter.getContext());
    }

    public void onEventMainThread(HeaderCountChangedEvent event) {
        view.setRequestsCount(getFriendsRequestsCount());
    }

    public int getFriendsRequestsCount() {
        return db.getFriendsRequestsCount();
    }

    public int getUnreadConversationCount() {
        return unreadConversationCount;
    }

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

    public void selectPhoto(@NonNull PhotoGalleryModel model) {
        presenterHelper.selectPhoto(model);
    }

    public void attachSelectedSuggestionPhotos() {
        Observable.from(getSelectedSuggestionPhotos())
                .map(element -> {
                    Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, element.getOriginalPath());
                    return new PhotoGalleryModel(pair.first, pair.second);
                })
                .map(photoGalleryModel -> {
                    ArrayList<PhotoGalleryModel> chosenImages = new ArrayList<>();
                    chosenImages.add(photoGalleryModel);
                    return new MediaAttachment(chosenImages, PickImageDelegate.PICK_PICTURE, CreateFeedPostPresenter.REQUEST_ID);
                })
                .compose(new IoToMainComposer<>())
                .subscribe(mediaAttachment -> {
                    mediaPickerManager.attach(mediaAttachment);
                }, error -> {
                    Timber.e(error, "");
                });
    }

    public List<PhotoGalleryModel> getSelectedSuggestionPhotos() {
        return presenterHelper.selectedPhotos();
    }

    public long lastSyncTimestamp() {
        return presenterHelper.lastSyncTime();
    }

    public void onEvent(DownloadPhotoEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DownloadImageCommand(context, event.url));
    }

    public void onEvent(EditBucketEvent event) {
        if (!view.isVisibleOnScreen()) return;
        //
        BucketBundle bundle = new BucketBundle();
        bundle.setType(event.type());
        bundle.setBucketItem(event.bucketItem());

        view.showEdit(bundle);
    }

    public void onEvent(DeleteBucketEvent event) {
        if (view.isVisibleOnScreen()) {
            BucketItem item = event.getEntity();

            view.bind(bucketInteractor.deleteItemPipe()
                    .createObservable(new DeleteItemHttpAction(item.getUid()))
                    .observeOn(AndroidSchedulers.mainThread()))
                    .subscribe(new ActionStateSubscriber<DeleteItemHttpAction>()
                            .onSuccess(deleteItemAction -> itemDeleted(item)));
        }
    }

    private void itemDeleted(FeedEntity feedEntity) {
        List<FeedItem> filteredItems = Queryable.from(feedItems)
                .filter(element -> !element.getItem().equals(feedEntity))
                .toList();

        feedItems.clear();
        feedItems.addAll(filteredItems);

        view.refreshFeedItems(feedItems);
    }

    public void onEvent(FeedEntityDeletedEvent event) {
        itemDeleted(event.getEventModel());
    }

    public void onEvent(FeedItemAddedEvent event) {
        feedItems.add(0, event.getFeedItem());
        view.refreshFeedItems(feedItems);
    }

    public void onEvent(FeedEntityChangedEvent event) {
        Queryable.from(feedItems).forEachR(item -> {
            if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
                FeedEntity feedEntity = event.getFeedEntity();
                if (feedEntity.getOwner() == null) {
                    feedEntity.setOwner(item.getItem().getOwner());
                }
                item.setItem(feedEntity);
            }
        });

        view.refreshFeedItems(feedItems);
    }

    public void onEvent(FeedEntityCommentedEvent event) {
        Queryable.from(feedItems).forEachR(item -> {
            if (item.getItem() != null && item.getItem().equals(event.getFeedEntity())) {
                item.setItem(event.getFeedEntity());
            }
        });

        view.refreshFeedItems(feedItems);
    }

    public void onEvent(LikesPressedEvent event) {
        if (view.isVisibleOnScreen()) {
            FeedEntity model = event.getModel();
            if (model.isLiked()) {
                entityManager.unlike(model);
            } else {
                entityManager.like(model);
            }
        }
    }

    public void onEvent(EntityLikedEvent event) {
        itemLiked(event.getFeedEntity());
    }

    public void onEvent(DeletePostEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePostCommand(event.getEntity().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));
    }

    public void onEvent(DeletePhotoEvent event) {
        if (view.isVisibleOnScreen())
            doRequest(new DeletePhotoCommand(event.getEntity().getUid()),
                    aVoid -> itemDeleted(event.getEntity()));

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

    private void itemLiked(FeedEntity feedEntity) {
        Queryable.from(feedItems).forEachR(feedItem -> {
            FeedEntity item = feedItem.getItem();
            if (item.getUid().equals(feedEntity.getUid())) {
                item.syncLikeState(feedEntity);
            }
        });

        view.refreshFeedItems(feedItems);
    }

    public interface View extends RxView, UidItemDelegate.View {

        <T> rx.Observable<T> bindUntilStop(rx.Observable<T> observable);

        void setRequestsCount(int count);

        void setUnreadConversationCount(int count);

        void refreshFeedItems(List<FeedItem> events);

        void refreshFeedItems(List<FeedItem> feedItems, List<PhotoGalleryModel> suggestedPhotos);

        void startLoading();

        void finishLoading();

        void showEdit(BucketBundle bucketBundle);

        void updateLoadingStatus(boolean loading, boolean noMoreElements);
    }
}