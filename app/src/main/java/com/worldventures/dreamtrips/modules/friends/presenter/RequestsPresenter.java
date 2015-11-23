package com.worldventures.dreamtrips.modules.friends.presenter;

import com.innahema.collections.query.functions.Action1;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.DeleteRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.GetRequestsQuery;
import com.worldventures.dreamtrips.modules.friends.api.ResponseBatchRequestCommand;
import com.worldventures.dreamtrips.modules.friends.events.AcceptAllRequestsEvent;
import com.worldventures.dreamtrips.modules.friends.events.AcceptRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.CancelRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.HideRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.RejectRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.ReloadFriendListEvent;
import com.worldventures.dreamtrips.modules.friends.events.RequestsLoadedEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.friends.model.RequestHeaderModel;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.common.model.User.Relationship.INCOMING_REQUEST;
import static com.worldventures.dreamtrips.modules.common.model.User.Relationship.OUTGOING_REQUEST;
import static com.worldventures.dreamtrips.modules.common.model.User.Relationship.REJECT;
import static com.worldventures.dreamtrips.modules.friends.api.ResponseBatchRequestCommand.RequestEntity;

public class RequestsPresenter extends Presenter<RequestsPresenter.View> {

    @Inject
    SnappyRepository snappyRepository;

    List<Circle> circles;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        circles = snappyRepository.getCircles();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadRequests();
    }

    public void reloadRequests() {
        view.startLoading();
        doRequest(new GetRequestsQuery(),
                items -> {
                    view.finishLoading();
                    eventBus.post(new RequestsLoadedEvent(Queryable.from(items)
                            .filter(item -> item.getRelationship() == INCOMING_REQUEST)
                            .toList().size()));
                    setItems(items);
                }, exception -> {
                    view.finishLoading();
                    handleError(exception);
                });
    }

    public void onEvent(UserClickedEvent event) {
        if (view.isVisibleOnScreen())
            view.openUser(new UserBundle(event.getUser()));
    }

    public void onEvent(AcceptAllRequestsEvent event) {
        view.showAddFriendDialog(circles, position -> {
            if (view.isVisibleOnScreen()) {
                view.startLoading();
                List<RequestEntity> responses = Queryable.from(view.getAdapter().getItems())
                        .filter(e -> e instanceof User && ((User) e).getRelationship() == INCOMING_REQUEST)
                        .map(element -> {
                            return new RequestEntity(((User) element).getId(),
                                    ActOnRequestCommand.Action.CONFIRM.name()
                                    , circles.get(position).getId());
                        }).toList();
                doRequest(new ResponseBatchRequestCommand(responses), jsonObject -> {
                    reloadRequests();
                    eventBus.post(new ReloadFriendListEvent());
                }, spiceException -> {
                    RequestsPresenter.this.handleError(spiceException);
                    view.finishLoading();
                });
            }
        });
    }

    private void setItems(List<User> items) {
        if (items != null) {
            List<User> incoming = Queryable.from(items).filter(item -> item.getRelationship() == INCOMING_REQUEST).toList();
            List<Object> sortedItems = new ArrayList<>();
            RequestHeaderModel incomingHeader = new RequestHeaderModel(context.getString(R.string.request_incoming), true);
            incomingHeader.setCount(incoming.size());
            sortedItems.add(incomingHeader);
            sortedItems.addAll(incoming);

            sortedItems.add(new RequestHeaderModel(context.getString(R.string.request_outgoing)));
            sortedItems.addAll(Queryable.from(items).filter(item ->
                    (item.getRelationship() == OUTGOING_REQUEST || item.getRelationship() == REJECT))
                    .toList());
            view.getAdapter().setItems(sortedItems);
        }
    }

    public void onEvent(AcceptRequestEvent event) {
        view.showAddFriendDialog(circles, position -> {
            view.startLoading();
            doRequest(new ActOnRequestCommand(event.getUser().getId(),
                            ActOnRequestCommand.Action.CONFIRM.name(),
                            circles.get(position).getId()),
                    object -> {
                        eventBus.post(new ReloadFriendListEvent());
                        onSuccess(event.getPosition());
                    },
                    this::onError);
        });
    }

    public void onEvent(CancelRequestEvent event) {
        view.startLoading();
        doRequest(new DeleteRequestCommand(event.getUser().getId()),
                object -> onSuccess(event.getPosition()),
                this::onError);
    }

    public void onEvent(HideRequestEvent event) {
        view.startLoading();
        doRequest(new DeleteRequestCommand(event.getUser().getId()),
                object -> onSuccess(event.getPosition()),
                this::onError);
    }

    public void onEvent(RejectRequestEvent event) {
        view.startLoading();
        doRequest(new ActOnRequestCommand(event.getUser().getId(),
                        ActOnRequestCommand.Action.REJECT.name()),
                object -> onSuccess(event.getPosition()),
                this::onError);
    }

    private void onSuccess(int position) {
        if (view != null) {
            view.finishLoading();
            if (position < view.getAdapter().getItemCount()) {
                view.getAdapter().remove(position);
                view.getAdapter().notifyItemRemoved(position);
            }
        }
    }

    private void onError(SpiceException exception) {
        if (view != null) {
            view.finishLoading();
            handleError(exception);
        }
    }

    public interface View extends Presenter.View {
        void startLoading();

        void openUser(UserBundle userBundle);

        void finishLoading();

        void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectAction);

        BaseArrayListAdapter<Object> getAdapter();
    }
}
