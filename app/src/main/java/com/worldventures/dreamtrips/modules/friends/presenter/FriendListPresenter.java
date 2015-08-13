package com.worldventures.dreamtrips.modules.friends.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.api.UnfriendCommand;
import com.worldventures.dreamtrips.modules.friends.events.OpenFriendPrefsEvent;
import com.worldventures.dreamtrips.modules.friends.events.QueryStickyEvent;
import com.worldventures.dreamtrips.modules.friends.events.ReloadFriendListEvent;
import com.worldventures.dreamtrips.modules.friends.events.RemoveUserEvent;
import com.worldventures.dreamtrips.modules.friends.events.UnfriendEvent;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.Friend;
import com.worldventures.dreamtrips.modules.profile.event.FriendGroupRelationChangedEvent;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.Icicle;


public class FriendListPresenter extends Presenter<FriendListPresenter.View> {

    private static final int PER_PAGE = 20;

    private int previousTotal = 0;
    private boolean loading = true;

    private List<Circle> circles;

    @Icicle
    Circle selectedCircle;
    @Icicle
    String query;
    @Icicle
    int position = 0;

    @Inject
    SnappyRepository snappyRepository;


    private DreamSpiceAdapterController<Friend> adapterController = new DreamSpiceAdapterController<Friend>() {
        @Override
        public SpiceRequest<ArrayList<Friend>> getReloadRequest() {
            return new GetFriendsQuery(selectedCircle, query, 0);
        }

        @Override
        public SpiceRequest<ArrayList<Friend>> getNextPageRequest(int currentCount) {
            return new GetFriendsQuery(selectedCircle, query, currentCount / PER_PAGE + 1);
        }

        @Override
        protected void onNextItemsLoaded(ArrayList<Friend> friends) {
            Queryable.from(friends).forEachR(friend -> friend.setCircles(circles));
            super.onNextItemsLoaded(friends);
        }

        @Override
        protected void onRefresh(ArrayList<Friend> friends) {
            Queryable.from(friends).forEachR(friend -> friend.setCircles(circles));
            super.onRefresh(friends);
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<Friend> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading();
                if (spiceException != null) {
                    handleError(spiceException);
                } else {
                    if (type.equals(LoadType.RELOAD)) {
                        resetLazyLoadFields();
                    }
                }
            }
        }
    };

    @Override
    public void takeView(View view) {
        super.takeView(view);
        circles = snappyRepository.getCircles();
        circles.add(0, Circle.all(context.getString(R.string.show_all)));
    }

    @Override
    public void onResume() {
        if (view.getAdapter().getCount() == 0) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
            adapterController.reload();
        }
        RemoveUserEvent event = eventBus.getStickyEvent(RemoveUserEvent.class);
        if (event != null) {
            eventBus.removeStickyEvent(event);
            if (view != null)
                view.getAdapter().remove(Queryable.from(view.getAdapter().getItems())
                        .firstOrDefault(friend -> friend.getId() == event.getUser().getId()));
        }
    }

    @Override
    public void dropView() {
        adapterController.setAdapter(null);
        super.dropView();
    }

    public void onFilterClicked() {
        view.showFilters(circles, position);
    }

    public void globalSearch() {
        eventBus.postSticky(new QueryStickyEvent(query));
        activityRouter.openFriendsSearch();
    }

    public void reload() {
        adapterController.reload();
    }

    public void reloadWithFilter(int position) {
        this.position = position;
        selectedCircle = position != 0 ? circles.get(position) : null;
        reload();
    }

    public void setQuery(String query) {
        adapterController.cancelRequest();
        this.query = query;
        reload();
    }

    private void resetLazyLoadFields() {
        previousTotal = 0;
        loading = false;
    }

    public void onEvent(ReloadFriendListEvent event) {
        reload();
    }

    public void onEvent(UnfriendEvent event) {
        unfriend(event.getFriend());
    }

    public void onEvent(OpenFriendPrefsEvent event) {
        activityRouter.openFriendPrefs(event.getFriend());
    }

    public void onEvent(FriendGroupRelationChangedEvent event) {
        for (Friend friend : view.getAdapter().getItems()) {
            if (friend.getId() == event.getFriend().getId()) {
                switch (event.getState()) {
                    case REMOVED:
                        friend.getCircleIds().remove(event.getCircle().getId());
                        break;
                    case ADDED:
                        friend.getCircleIds().add(event.getCircle().getId());
                        break;
                }
                friend.setCircles(snappyRepository.getCircles());
            }
        }
        view.getAdapter().notifyDataSetChanged();
    }


    public void scrolled(int totalItemCount, int lastVisible) {
        if (totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
        }
        if (!loading
                && lastVisible == totalItemCount - 1
                && totalItemCount % PER_PAGE == 0) {
            adapterController.loadNext();
            loading = true;
        }
    }

    public String getQuery() {
        return query;
    }

    private void unfriend(Friend user) {
        view.startLoading();
        doRequest(new UnfriendCommand(user.getId()), object -> {
            if (view != null) {
                view.finishLoading();
                view.getAdapter().remove(user);
            }
        });
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        if (view != null) view.finishLoading();
    }

    public interface View extends Presenter.View {

        BaseArrayListAdapter<Friend> getAdapter();

        void showFilters(List<Circle> circles, int selectedPosition);

        void finishLoading();

        void startLoading();
    }

}
