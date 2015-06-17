package com.worldventures.dreamtrips.modules.friends.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.AddUserRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.SearchUsersQuery;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;

import java.util.ArrayList;
import java.util.List;

public class FriendSearchPresenter extends Presenter<FriendSearchPresenter.View> {

    private static final int PER_PAGE = 20;

    private String query;

    private DreamSpiceAdapterController<User> adapterController = new DreamSpiceAdapterController<User>() {
        @Override
        public SpiceRequest<ArrayList<User>> getReloadRequest() {
            return new SearchUsersQuery(query, 0, PER_PAGE);
        }

        @Override
        public SpiceRequest<ArrayList<User>> getNextPageRequest(int currentCount) {
            return new SearchUsersQuery(query, currentCount / PER_PAGE + 1, PER_PAGE);
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<User> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading();
                if (spiceException != null) {
                    handleError(spiceException);
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (view.getAdapter().getCount() == 0) {
            adapterController.setSpiceManager(dreamSpiceManager);
            adapterController.setAdapter(view.getAdapter());
            adapterController.reload();
        }
    }

    public void onEvent(AddUserRequestEvent event) {
        doRequest(new AddUserRequestCommand(event.getUser().getId()),
                jsonObject -> onSuccess(event.getPosition()),
                this::onError);
    }

    private void onSuccess(int position) {
        if (view != null) {
            view.getAdapter().remove(position);
            view.getAdapter().notifyItemRemoved(position);
        }
    }

    private void onError(SpiceException exception) {
        if (view != null) {
            view.getAdapter().notifyDataSetChanged();
            handleError(exception);
        }
    }

    public void reload() {
        this.query = null;
        adapterController.reload();
    }

    public void setQuery(String query) {
        this.query = query;
        adapterController.reload();
    }

    public interface View extends Presenter.View {

        BaseArrayListAdapter<User> getAdapter();

        void finishLoading();

        void startLoading();
    }

}
