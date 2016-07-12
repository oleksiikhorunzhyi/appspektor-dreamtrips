package com.worldventures.dreamtrips.modules.feed.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.DeleteItemHttpAction;
import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.UidItemDelegate;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.feed.api.DeletePostCommand;
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
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.modules.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.api.DownloadImageCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.helper.ActionStateToActionTransformer;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class FeedHashtagPresenter<T extends FeedHashtagPresenter.View> extends JobPresenter<T> {

    private final static int FEEDS_PER_PAGE = 10;
    private final int MIN_QUERY_LENGTH = 3;

    @State
    String query;
    @State
    protected ArrayList<FeedItem> feedItems = new ArrayList<>();
    @State
    protected ArrayList<HashtagSuggestion> hashtagSuggestions = new ArrayList<>();

    @Inject
    protected HashtagInteractor interactor;
    @Inject
    protected FeedEntityManager entityManager;
    @Inject
    BucketInteractor bucketInteractor;

    private UidItemDelegate uidItemDelegate;

    public FeedHashtagPresenter() {
        uidItemDelegate = new UidItemDelegate(this);
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        if (feedItems.size() != 0) {
            view.refreshFeedItems(feedItems);
        }
        subscribeRefreshFeeds();
        subscribeLoadNextFeeds();

        view.bind(interactor.getSuggestionPipe()
                .observeSuccess()
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(command -> {
                    view.onSuggestionsReceived(command.getFullQueryText(), command.getResult());
                    view.hideSuggestionProgress();
                }, throwable -> {
                    Timber.e(throwable, "");
                    view.hideSuggestionProgress();
                });

        view.onSuggestionsReceived(query, hashtagSuggestions);
    }

    @Override
    public void onInjected() {
        super.onInjected();
        entityManager.setRequestingPresenter(this);
    }

    @Nullable
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void searchSuggestions(String fullText, String world) {
        if (world.replaceAll("#", "").length() >= MIN_QUERY_LENGTH) {
            view.showSuggestionProgress();
            interactor.getSuggestionPipe().send(new HashtagSuggestionCommand(fullText, world));
        } else {
            view.clearSuggestions();
            view.hideSuggestionProgress();

        }
    }

    public void onRefresh() {
        if (!TextUtils.isEmpty(query)) {
            view.startLoading();
            interactor.getRefreshFeedsByHashtagsPipe().send(new FeedByHashtagCommand.Refresh(query, FEEDS_PER_PAGE));
        }
    }

    public void loadNext() {
        if (feedItems.size() > 0) {
            if (!TextUtils.isEmpty(query)) {
                interactor.getLoadNextFeedsByHashtagsPipe().send(new FeedByHashtagCommand.LoadNext(query, FEEDS_PER_PAGE, feedItems.get(feedItems.size() - 1).getCreatedAt()));
            }
        }
    }

    private void subscribeRefreshFeeds() {
        view.bind(interactor.getRefreshFeedsByHashtagsPipe().observe()
                .compose(new ActionStateToActionTransformer<>())
                .map(FeedByHashtagCommand.Refresh::getResult)
                .compose(new IoToMainComposer<>()))
                .subscribe(dataMetaData -> refreshFeedSucceed(dataMetaData.getParentFeedItems()),
                        throwable -> {
                            refreshFeedError();
                            Timber.e(throwable, "");
                        }
                );
    }

    private void refreshFeedSucceed(ArrayList<ParentFeedItem> freshItems) {
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
        view.refreshFeedItems(feedItems);
    }

    private void refreshFeedError() {
        view.updateLoadingStatus(false, false);
        view.finishLoading();
        view.refreshFeedItems(feedItems);
    }

    private void subscribeLoadNextFeeds() {
        view.bind(interactor.getLoadNextFeedsByHashtagsPipe().observe()
                .compose(new ActionStateToActionTransformer<>())
                .map(FeedByHashtagCommand.LoadNext::getResult)
                .compose(new IoToMainComposer<>()))
                .subscribe(dataMetaData -> {
                            addFeedItems(dataMetaData.getParentFeedItems());
                        },
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

        void startLoading();

        void finishLoading();

        void refreshFeedItems(List<FeedItem> events);

        void updateLoadingStatus(boolean loading, boolean noMoreElements);

        void onSuggestionsReceived(String fullQueryText, @NonNull List<HashtagSuggestion> suggestionList);

        void clearSuggestions();

        void showEdit(BucketBundle bucketBundle);

        void showSuggestionProgress();

        void hideSuggestionProgress();
    }
}
