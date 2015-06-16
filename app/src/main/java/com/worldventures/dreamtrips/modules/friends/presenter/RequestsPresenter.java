package com.worldventures.dreamtrips.modules.friends.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.friends.api.GetRequestsQuery;
import com.worldventures.dreamtrips.modules.friends.events.RequestsLoadedEvent;
import com.worldventures.dreamtrips.modules.friends.model.Request;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

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

    public interface View extends Presenter.View {
        void startLoading();

        void finishLoading();

        BaseArrayListAdapter<Object> getAdapter();
    }
}
