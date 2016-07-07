package com.worldventures.dreamtrips.modules.feed.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.modules.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.FeedByHashtagCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
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
                    view.onSuggestionsReceived(command.getResult());
                }, throwable -> {
                    Timber.e(throwable, "");
                });

        view.onSuggestionsReceived(hashtagSuggestions);
    }

    @Nullable
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void searchSuggestions(String world) {
        if (world.length() >= MIN_QUERY_LENGTH) {
            interactor.getSuggestionPipe().send(new HashtagSuggestionCommand(world));
        } else {
            view.clearSuggestions();
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
        addFeedItems(new ArrayList<>());
    }

    public interface View extends RxView {

        void startLoading();

        void finishLoading();

        void refreshFeedItems(List<FeedItem> events);

        void updateLoadingStatus(boolean loading, boolean noMoreElements);

        void onSuggestionsReceived(@NonNull List<HashtagSuggestion> suggestionList);

        void clearSuggestions();
    }
}
