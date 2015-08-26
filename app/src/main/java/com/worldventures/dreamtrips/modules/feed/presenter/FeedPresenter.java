package com.worldventures.dreamtrips.modules.feed.presenter;

import com.badoo.mobile.util.WeakHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.events.EntityLikedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.GetAccountFeedQuery;
import com.worldventures.dreamtrips.modules.feed.api.LikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.api.UnlikeEntityCommand;
import com.worldventures.dreamtrips.modules.feed.event.FeedObjectChangedEvent;
import com.worldventures.dreamtrips.modules.feed.event.LikesPressedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseEventModel;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedModel;
import com.worldventures.dreamtrips.modules.profile.event.profilecell.OnFeedReloadEvent;

import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class FeedPresenter extends Presenter<FeedPresenter.View> {

    private int previousTotal = 0;
    private boolean loading = true;

    public FeedPresenter() {
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        reloadFeed();
    }

    public void onEvent(FeedObjectChangedEvent event) {
        view.getAdapter().itemUpdated(event.getFeedObject());
    }

    public void onEvent(LikesPressedEvent event) {
        BaseEventModel model = event.getModel();
        Timber.d("Like item " + model.getItem().getUid());
        DreamTripsRequest command = model.getItem().isLiked() ?
                new UnlikeEntityCommand(model.getItem().getUid()) :
                new LikeEntityCommand(model.getItem().getUid());
        doRequest(command, element -> itemLiked(model));
    }

    //TODO Refactor this after apperean release
    public void onEvent(EntityLikedEvent event) {
        BaseEventModel model = Queryable.from(view.getAdapter().getItems())
                .firstOrDefault(element -> element.getItem().getUid().equals(event.getId()));

        if (model != null) {
            itemLiked(model);
        }
    }

    private void itemLiked(BaseEventModel model) {
        model.getItem().setLiked(!model.getItem().isLiked());
        int likesCount = model.getItem().getLikesCount();

        if (model.getItem().isLiked()) likesCount++;
        else likesCount--;

        model.getItem().setLikesCount(likesCount);

        itemChanged(model);
    }

    private void itemChanged(BaseEventModel baseFeedModel) {
        view.getAdapter().itemUpdated(baseFeedModel);
    }

    public void reloadFeed() {
        view.startLoading();
        view.setEmptyViewVisibility(false);
        doRequest(new GetAccountFeedQuery(Calendar.getInstance().getTime()),
                this::itemsLoaded, spiceException -> {
                    this.handleError(spiceException);
                    view.finishLoading();
                    view.setEmptyViewVisibility(true);
                });
    }

    public void scrolled(int totalItemCount, int lastVisible) {
        if (featureManager.available(Feature.SOCIAL)) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }

            if (!loading
                    && lastVisible == totalItemCount - 1) {
                loading = true;
                loadMore();
            }
        }
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.finishLoading();
    }

    private void loadMore() {
        if (view.getAdapter().getItemCount() > 0) {
            doRequest(new GetAccountFeedQuery(view.getAdapter()
                            .getItem(view.getAdapter().getItemCount() - 1).getCreatedAt()),
                    this::addFeedItems);
        }
    }

    public void onEvent(OnFeedReloadEvent event) {
        new WeakHandler().postDelayed(this::reloadFeed, 100);
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

        BaseArrayListAdapter<BaseEventModel> getAdapter();

        void setEmptyViewVisibility(boolean visible);
    }
}
