package com.worldventures.dreamtrips.modules.friends.presenter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Action1;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.AddUserRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.SearchUsersQuery;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FriendSearchPresenter extends Presenter<FriendSearchPresenter.View> {

    private static final int PER_PAGE = 20;

    private String query;

    @Inject
    SnappyRepository snappyRepository;
    private List<Circle> circles;

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
    public void takeView(View view) {
        super.takeView(view);
        circles = snappyRepository.getCircles();
    }

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
        view.showAddFriendDialog(circles, position ->
                        addFriend(event.getUser(), circles.get(position), event.getPosition())
        );
    }

    private void addFriend(User user, Circle circle, int position) {
        doRequest(new AddUserRequestCommand(user.getId(), circle),
                jsonObject -> onSuccess(position),
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

        void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectAction);
    }

}
