package com.worldventures.dreamtrips.modules.friends.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.model.UserWrapper;

import java.util.ArrayList;
import java.util.List;

public class AccountFriendsPresenter extends Presenter<AccountFriendsPresenter.View> {

    private DreamSpiceAdapterController<UserWrapper> adapterController = new DreamSpiceAdapterController<UserWrapper>() {
        @Override
        public SpiceRequest<ArrayList<UserWrapper>> getReloadRequest() {
            return new GetFriendsQuery();
        }

        @Override
        protected void onRefresh(ArrayList<UserWrapper> successStories) {
            super.onRefresh(performFiltering(successStories));
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<UserWrapper> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading(items);
                if (spiceException != null) {
                    handleError(spiceException);
                }
            }
        }
    };

    private ArrayList<UserWrapper> performFiltering(ArrayList<UserWrapper> users) {
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

    public void reload() {
        adapterController.reload();
    }


    public interface View extends Presenter.View {

        IRoboSpiceAdapter<UserWrapper> getAdapter();

        void finishLoading(List<UserWrapper> items);

        void startLoading();
    }

}
