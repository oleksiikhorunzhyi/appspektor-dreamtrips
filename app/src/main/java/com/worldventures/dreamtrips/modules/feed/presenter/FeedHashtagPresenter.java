package com.worldventures.dreamtrips.modules.feed.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.feed.command.GetFeedsByHashtagsCommand;
import com.worldventures.dreamtrips.modules.feed.model.DataMetaData;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.feed.presenter.interactor.HashtagInteractor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateToActionTransformer;
import timber.log.Timber;

public class FeedHashtagPresenter<T extends FeedHashtagPresenter.View> extends JobPresenter<T> {

    private static final int MIN_QUERY_LENGTH = 3;
    private final static int FEEDS_PER_PAGE = 10;

    @State
    protected ArrayList<FeedItem> feedItems;

    @Inject
    HashtagInteractor hashtagInteractor;

    private ActionPipe<GetFeedsByHashtagsCommand> refreshFeedsByHashtagsPipe;
    private ActionPipe<GetFeedsByHashtagsCommand> loadNextFeedsByHashtagsPipe;

    @Override
    public void takeView(T view) {
        super.takeView(view);
        if (feedItems.size() != 0) {
            view.refreshFeedItems(feedItems);
        }
        refreshFeedsByHashtagsPipe = hashtagInteractor.getRefreshFeedsByHashtagsPipe();
        loadNextFeedsByHashtagsPipe = hashtagInteractor.getLoadNextFeedsByHashtagsPipe();
        subscribeRefreshFeeds();
        subscribeLoadNextFeeds();
    }

    @Override
    public void restoreInstanceState(Bundle savedState) {
        super.restoreInstanceState(savedState);
        if (savedState == null) feedItems = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    public void onRefresh() {
        String query = view.getQuery();
        if (!TextUtils.isEmpty(query)) {
            view.startLoading();
            refreshFeedsByHashtagsPipe.send(new GetFeedsByHashtagsCommand(query, FEEDS_PER_PAGE, null));
        }
    }

    public void loadNext() {
        if (feedItems.size() > 0) {
            String query = view.getQuery();
            if (!TextUtils.isEmpty(query)) {
                view.startLoading();
                loadNextFeedsByHashtagsPipe.send(new GetFeedsByHashtagsCommand(query, FEEDS_PER_PAGE, getLastDate()));
            }
        }
    }

    private void subscribeRefreshFeeds() {
        view.bind(refreshFeedsByHashtagsPipe.observe()
                .compose(new ActionStateToActionTransformer<>())
                .map(GetFeedsByHashtagsCommand::getResult)
                .compose(new IoToMainComposer<>()))
                .subscribe(this::refreshFeedSucceed,
                        throwable -> {
                            refreshFeedError();
                            Timber.e(throwable, "");
                        });
    }

    private void refreshFeedSucceed(DataMetaData dataMetaData) {
        ArrayList<ParentFeedItem> freshItems = dataMetaData.getParentFeedItems();
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
        view.finishLoading();
        view.refreshFeedItems(feedItems);
    }

    private void subscribeLoadNextFeeds() {
        view.bind(loadNextFeedsByHashtagsPipe.observe()
                .compose(new ActionStateToActionTransformer<>())
                .map(GetFeedsByHashtagsCommand::getResult)
                .compose(new IoToMainComposer<>()))
                .subscribe(dataMetaData ->
                                addFeedItems(dataMetaData.getParentFeedItems()),
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
        addFeedItems(new ArrayList<>());
    }

    @Nullable
    private Date getLastDate() {
        if (feedItems.isEmpty()) return null;
        FeedItem<FeedEntity> feedItem = feedItems.get(feedItems.size() - 1);
        FeedItem<FeedEntity> lastFeedItem = Queryable.from(feedItem).lastOrDefault(new Predicate<FeedItem<FeedEntity>>() {
            Date last;

            @Override
            public boolean apply(FeedItem<FeedEntity> element) {
                if (last == null || element == null) return false;
                return last.before(element.getCreatedAt());
            }
        });

        return lastFeedItem != null ? lastFeedItem.getCreatedAt() : null;
    }

    public interface View extends RxView {

        @Nullable
        String getQuery();

        void startLoading();

        void finishLoading();

        void refreshFeedItems(List<FeedItem> events);

        void updateLoadingStatus(boolean loading, boolean noMoreElements);
    }
}
