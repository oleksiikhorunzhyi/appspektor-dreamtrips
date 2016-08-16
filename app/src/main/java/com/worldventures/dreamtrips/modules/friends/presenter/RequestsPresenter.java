package com.worldventures.dreamtrips.modules.friends.presenter;

import android.support.annotation.StringRes;

import com.innahema.collections.query.functions.Action1;
import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.DeleteRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.GetRequestsQuery;
import com.worldventures.dreamtrips.modules.friends.api.ResponseBatchRequestCommand;
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

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.dreamtrips.modules.common.model.User.Relationship.INCOMING_REQUEST;
import static com.worldventures.dreamtrips.modules.common.model.User.Relationship.OUTGOING_REQUEST;
import static com.worldventures.dreamtrips.modules.common.model.User.Relationship.REJECTED;
import static com.worldventures.dreamtrips.modules.friends.api.ResponseBatchRequestCommand.RequestEntity;

public class RequestsPresenter extends Presenter<RequestsPresenter.View> {

    @Inject
    CirclesInteractor circlesInteractor;

    @Override
    public void onResume() {
        super.onResume();
        reloadRequests();
    }

    private void onCirclesStart() {
        view.showBlockingProgress();
    }

    private void onCirclesError(@StringRes String messageId) {
        view.hideBlockingProgress();
        view.informUser(messageId);
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

    private void setItems(List<User> items) {
        if (items != null) {
            List<Object> sortedItems = new ArrayList<>();

            List<User> incoming = Queryable.from(items).filter(item -> item.getRelationship() == INCOMING_REQUEST).toList();
            if (!incoming.isEmpty()) {
                RequestHeaderModel incomingHeader = new RequestHeaderModel(context.getString(R.string.request_incoming_long), true);
                incomingHeader.setCount(incoming.size());
                sortedItems.add(incomingHeader);
                sortedItems.addAll(incoming);
            }

            List<User> outgoing = Queryable.from(items).filter(item ->
                    (item.getRelationship() == OUTGOING_REQUEST || item.getRelationship() == REJECTED))
                    .toList();
            if (!outgoing.isEmpty()) {
                sortedItems.add(new RequestHeaderModel(context.getString(R.string.request_outgoing_long)));
                sortedItems.addAll(outgoing);
            }

            view.getAdapter().setItems(sortedItems);
        }
    }

    public void acceptAllRequests() {
        circlesInteractor.pipe()
                .observe()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(new ActionStateSubscriber<CirclesCommand>()
                        .onStart(circlesCommand -> onCirclesStart())
                        .onSuccess(circlesCommand -> acceptAllCirclesSuccess(circlesCommand.getResult()))
                        .onFail((circlesCommand, throwable) -> onCirclesError(circlesCommand.getErrorMessage())));
    }

    private void acceptAllCirclesSuccess(List<Circle> circles) {
        view.hideBlockingProgress();
        view.showAddFriendDialog(circles, position -> {
            if (view.isVisibleOnScreen()) {
                view.startLoading();
                List<RequestEntity> responses = Queryable.from(view.getAdapter().getItems())
                        .filter(e -> e instanceof User && ((User) e).getRelationship() == INCOMING_REQUEST)
                        .map(element -> {
                            return new RequestEntity(((User) element).getId(),
                                    ActOnRequestCommand.Action.CONFIRM.name(),
                                    circles.get(position).getId());
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

    public void acceptRequest(User user) {
        circlesInteractor.pipe()
                .observe()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(new ActionStateSubscriber<CirclesCommand>()
                        .onStart(circlesCommand -> onCirclesStart())
                        .onSuccess(circlesCommand -> acceptCirclesSuccess(user, circlesCommand.getResult()))
                        .onFail((circlesCommand, throwable) -> onCirclesError(circlesCommand.getErrorMessage())));
    }

    private void acceptCirclesSuccess(User user, List<Circle> circles) {
        view.hideBlockingProgress();
        view.showAddFriendDialog(circles, position -> {
            view.startLoading();
            doRequest(new ActOnRequestCommand(user.getId(),
                            ActOnRequestCommand.Action.CONFIRM.name(),
                            circles.get(position).getId()),
                    object -> {
                        eventBus.post(new ReloadFriendListEvent());
                        onSuccess(user);
                        reloadRequests();
                        updateRequestsCount();
                    }, this::onError);
        });
    }

    public void rejectRequest(User user) {
        eventBus.post(new RejectRequestEvent(user));
        view.startLoading();
        doRequest(new ActOnRequestCommand(user.getId(),
                        ActOnRequestCommand.Action.REJECT.name()),
                object -> {
                    onSuccess(user);
                    updateRequestsCount();
                    reloadRequests();
                },
                this::onError);
    }

    public void hideRequest(User user) {
        eventBus.post(new HideRequestEvent(user));
        deleteRequest(user, DeleteRequestCommand.Action.HIDE);
        reloadRequests();
    }

    public void cancelRequest(User user) {
        eventBus.post(new CancelRequestEvent(user));
        deleteRequest(user, DeleteRequestCommand.Action.CANCEL);
        reloadRequests();
    }

    private void deleteRequest(User user, DeleteRequestCommand.Action action) {
        view.startLoading();
        doRequest(new DeleteRequestCommand(user.getId(), action),
                object -> {
                    onSuccess(user);
                    reloadRequests();
                },
                this::onError);
    }

    private void onSuccess(User user) {
        if (view != null) {
            view.finishLoading();
            view.getAdapter().remove(user);
        }
    }

    private void onError(SpiceException exception) {
        if (view != null) {
            view.finishLoading();
            handleError(exception);
        }
    }

    private void updateRequestsCount() {
        if (view.getAdapter().getItem(0) instanceof RequestHeaderModel) {
            RequestHeaderModel model = ((RequestHeaderModel) view.getAdapter().getItem(0));
            model.setCount(Queryable.from(view.getAdapter().getItems())
                    .count(item -> item instanceof User && ((User) item).getRelationship() == INCOMING_REQUEST));
            view.getAdapter().notifyItemChanged(0);
        }
    }

    public interface View extends Presenter.View, BlockingProgressView {
        void startLoading();

        void openUser(UserBundle userBundle);

        void finishLoading();

        void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectAction);

        BaseArrayListAdapter<Object> getAdapter();
    }
}
