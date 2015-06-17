package com.worldventures.dreamtrips.modules.friends.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.model.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendListPresenter extends Presenter<FriendListPresenter.View> {

    private static final int PER_PAGE = 10;
    private FilterType filterType = FilterType.ALL;

    private DreamSpiceAdapterController<Friend> adapterController = new DreamSpiceAdapterController<Friend>() {
        @Override
        public SpiceRequest<ArrayList<Friend>> getReloadRequest() {
            return new GetFriendsQuery(filterType.query);
        }

        @Override
        public SpiceRequest<ArrayList<Friend>> getNextPageRequest(int currentCount) {
            return new GetFriendsQuery(filterType.query, PER_PAGE, currentCount / PER_PAGE + 1);
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

    public void globalSearch() {
        activityRouter.openFriendsSearch();
    }

    public void reload() {
        adapterController.reload();
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void reloadWithFilter(int itemId) {
        switch (itemId) {
            case R.id.action_social_show_all:
                filterType = FilterType.ALL;
                break;
            case R.id.action_social_friends:
                filterType = FilterType.FRIEND;
                break;
            case R.id.action_social_sponsored:
                filterType = FilterType.SPONSORED;
                break;
            case R.id.action_social_sponsors:
                filterType = FilterType.SPONSORS;
                break;
        }
        reload();
    }

    public enum FilterType {
        ALL("all"), FRIEND("friends"), SPONSORED("sponsored"), SPONSORS("sponsors");

        private String query;

        FilterType(String query) {
            this.query = query;
        }
    }

    public interface View extends Presenter.View {

        IRoboSpiceAdapter<Friend> getAdapter();

        void finishLoading(List<Friend> items);

        void startLoading();
    }

}
