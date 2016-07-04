package com.worldventures.dreamtrips.modules.feed.presenter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.innahema.collections.query.functions.Predicate;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.HumaneErrorTextFactory;
import com.worldventures.dreamtrips.modules.common.presenter.JobPresenter;
import com.worldventures.dreamtrips.modules.feed.command.GetFeedsByHashtagsCommand;
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
    protected ArrayList<FeedItem> feedItems = new ArrayList<>();

    private boolean loading;
    private boolean noMoreItems;

    @Inject
    HashtagInteractor hashtagInteractor;

    private ActionPipe<GetFeedsByHashtagsCommand> getFeedsByHashtagsPipe;

    @Override
    public void takeView(T view) {
        super.takeView(view);
        getFeedsByHashtagsPipe = hashtagInteractor.getFeedsByHashtagsPipe();
        subscribeGetFeeds();
    }

    public void scrolled(int totalItemCount, int lastVisible) {
        if (!loading && !noMoreItems && lastVisible == totalItemCount - 1) {
            loading = true;
            loadMoreFeeds();
        }
    }

    public void loadFeeds() {
        if (!TextUtils.isEmpty(view.getQuery()) && view.getQuery().length() >= MIN_QUERY_LENGTH) {
            loading = true;
            view.startLoading();
            getFeedsByHashtagsPipe.send(new GetFeedsByHashtagsCommand(view.getQuery(), FEEDS_PER_PAGE, getLastDate()));
        }
    }

    private void loadMoreFeeds() {
        if (feedItems.size() > 0) loadFeeds();
    }

    public void reloadFeeds() {
        view.clearFeedItems();
        feedItems.clear();
        loadFeeds();
    }

    private void subscribeGetFeeds() {
        view.bind(getFeedsByHashtagsPipe.observe()
                .compose(new ActionStateToActionTransformer<>())
                .map(GetFeedsByHashtagsCommand::getResult)
                .compose(new IoToMainComposer<>()))
                .subscribe(this::onFeedsLoaded,
                        throwable -> {
                            view.finishLoading();
                            view.informUser(new HumaneErrorTextFactory().create(throwable));
                            Timber.e(throwable, "");
                        });
    }

    private void onFeedsLoaded(List<ParentFeedItem> items) {
        feedItems.addAll(Queryable.from(items)
                .filter(ParentFeedItem::isSingle)
                .map(element -> element.getItems().get(0))
                .toList());

        view.addFeedItems(feedItems);
        view.finishLoading();
        noMoreItems = items.size() < FEEDS_PER_PAGE;
        loading = false;
    }

    @Nullable
    private Date getLastDate() {
        if (feedItems.isEmpty()) return null;
        FeedItem<FeedEntity> feedItem = feedItems.get(feedItems.size() - 1);
        FeedItem<FeedEntity> lastFeedItem = Queryable.from(feedItem).lastOrDefault(new Predicate<FeedItem<FeedEntity>>() {
            Date last;

            @Override
            public boolean apply(FeedItem<FeedEntity> element) {
                if (last == null) element.getCreatedAt();
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

        void addFeedItems(List items);

        void clearFeedItems();

        void addSuggestionItems(List items);

        void clearSuggestionItems();
    }
}
