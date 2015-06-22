package com.worldventures.dreamtrips.modules.friends.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.DeleteRequestCommand;
import com.worldventures.dreamtrips.modules.friends.api.GetRequestsQuery;
import com.worldventures.dreamtrips.modules.friends.events.AcceptRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.CancelRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.HideRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.RejectRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.RequestsLoadedEvent;
import com.worldventures.dreamtrips.modules.friends.model.Request;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RequestsPresenter extends Presenter<RequestsPresenter.View> {

    @Inject
    SnappyRepository snappyRepository;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        reloadRequests();
    }

    public void reloadRequests() {
        view.startLoading();
        doRequest(new GetRequestsQuery(),
                items -> {
                    view.finishLoading();
                    eventBus.post(new RequestsLoadedEvent(items.size()));
                    addItems(items);
                }, exception -> {
                    view.finishLoading();
                    handleError(exception);
                });
    }

    private void addItems(List<Request> items) {
        List<Object> sortedItems = new ArrayList<>();
        sortedItems.add(context.getString(R.string.request_incoming));
        sortedItems.addAll(Queryable.from(items).filter(item ->
                item.getDirection().equals(Request.INCOMING)).toList());
        sortedItems.add(context.getString(R.string.request_outgoing));
        sortedItems.addAll(Queryable.from(items).filter(item ->
                item.getDirection().equals(Request.OUTGOING)).toList());
        view.getAdapter().setItems(sortedItems);
    }

    public void onEvent(AcceptRequestEvent event) {
        doRequest(new ActOnRequestCommand(event.getUser().getId(), ActOnRequestCommand.Action.CONFIRM.name()),
                object -> onSuccess(event.getPosition()),
                this::onError);
    }

    public void onEvent(CancelRequestEvent event) {
        doRequest(new DeleteRequestCommand(event.getUser().getId()),
                object -> onSuccess(event.getPosition()),
                this::onError);
    }

    public void onEvent(HideRequestEvent event) {
        doRequest(new DeleteRequestCommand(event.getUser().getId()),
                object -> onSuccess(event.getPosition()),
                this::onError);
    }

    public void onEvent(RejectRequestEvent event) {
        doRequest(new ActOnRequestCommand(event.getUser().getId(), ActOnRequestCommand.Action.REJECT.name()),
                object -> onSuccess(event.getPosition()),
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

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        BaseArrayListAdapter<Object> getAdapter();
    }
}
