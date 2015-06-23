package com.worldventures.dreamtrips.modules.friends.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class FriendListPresenter extends Presenter<FriendListPresenter.View> {

    private static final int PER_PAGE = 10;

    private List<Circle> circles;
    private Circle selectedCircle;
    private String query;

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
        protected void onRefresh(ArrayList<Friend> friends) {
            super.onRefresh(performFiltering(friends));
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<Friend> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading(items);
                if (spiceException != null) {
                    handleError(spiceException);
                }
            }
        }
    };

    private ArrayList<Friend> performFiltering(ArrayList<Friend> users) {
        return users;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        circles = snappyRepository.getCircles();
    }

    @Override
    public void onResume() {
        if (view.getAdapter().getCount() == 0) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
            adapterController.reload();
        }
    }

    @Override
    public void dropView() {
        adapterController.setAdapter(null);
        super.dropView();
    }

    public void onFilterClicked() {
        view.showFilters(circles);
    }

    public void globalSearch() {
        activityRouter.openFriendsSearch();
    }

    public void reload() {
        adapterController.reload();
    }

    public void reloadWithFilter(int position) {
        selectedCircle = circles.get(position);
        reload();
    }

    public void setQuery(String query) {
        this.query = query;
        reload();
    }

    public interface View extends Presenter.View {

        IRoboSpiceAdapter<Friend> getAdapter();

        void showFilters(List<Circle> circles);

        void finishLoading(List<Friend> items);

        void startLoading();
    }

}
