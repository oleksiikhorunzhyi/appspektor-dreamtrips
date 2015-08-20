package com.worldventures.dreamtrips.modules.feed.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountFeedQuery;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountTimelineQuery;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.event.CommentsPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.event.FeedObjectChangedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFeedReloadEvent;

import java.util.List;

public class FeedPresenter extends Presenter<FeedPresenter.View> {

    private int previousTotal = 0;
    private boolean loading = true;

    public FeedPresenter() {
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        loadFeed();
    }

    public void onEvent(CommentsPressedEvent event) {
        eventBus.cancelEventDelivery(event);
        view.openComments(event.getModel());
    }

    public void onEvent(FeedObjectChangedEvent event) {
        view.getAdapter().itemUpdated(event.getFeedObject());
    }

    public void onEvent(LikesPressedEvent event) {
        BaseFeedModel model = event.getModel();
        DreamTripsRequest command = model.getItem().isLiked() ?
                new UnlikeEntityCommand(model.getItem().getUid()) :
                new LikeEntityCommand(model.getItem().getUid());
        doRequest(command, element -> {
            model.getItem().setLiked(!model.getItem().isLiked());
            itemChanged(model);
        });
    }

    private void itemChanged(BaseFeedModel baseFeedModel) {
        view.getAdapter().itemUpdated(baseFeedModel);
    }

    public void loadFeed() {
        view.startLoading();
        doRequest(new GetAccountFeedQuery(0), this::itemsLoaded);
    }

    public void scrolled(int totalItemCount, int lastVisible) {
        if (featureManager.available(Feature.SOCIAL)) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }

            if (!loading
                    && lastVisible == totalItemCount
                    && (totalItemCount) % GetAccountTimelineQuery.LIMIT == 0) {
                loading = true;

                doRequest(new GetAccountFeedQuery(previousTotal / GetAccountTimelineQuery.LIMIT),
                        this::addFeedItems);
            }
        }
    }

    public void onEvent(OnFeedReloadEvent event) {
        loadFeed();
    }

    private void itemsLoaded(List<ParentFeedModel> feedItems) {
        view.finishLoading();
        view.getAdapter().clear();
        view.getAdapter().addItems(Queryable.from(feedItems)
                .filter(ParentFeedModel::isSingle).map(element -> element.getItems().get(0)).toList());
    }

    private void addFeedItems(List<ParentFeedModel> feedItems) {
        view.getAdapter().addItems(Queryable.from(feedItems)
                .filter(ParentFeedModel::isSingle).map(element -> element.getItems().get(0)).toList());
    }

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        BaseArrayListAdapter<BaseFeedModel> getAdapter();

        void openComments(BaseFeedModel baseFeedModel);

        void openPost();
    }
}
