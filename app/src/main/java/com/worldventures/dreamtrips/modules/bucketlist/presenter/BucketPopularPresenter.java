package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import android.app.Activity;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetPopularLocation;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetPopularLocationQuery;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.model.ImmutableBucketBodyImpl;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.COMPLETED;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.NEW;

public class BucketPopularPresenter extends Presenter<BucketPopularPresenter.View> {
    @Inject
    Activity activity;

    @Inject
    BucketInteractor bucketInteractor;

    private BucketItem.BucketType type;

    private SweetDialogHelper sweetDialogHelper;

    public BucketPopularPresenter(BucketItem.BucketType type) {
        super();
        this.type = type;
        sweetDialogHelper = new SweetDialogHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (view.getAdapter().getCount() == 0) reload();
    }

    public void onSearch(String constraint) {
        if (constraint.length() > 2) {
            view.startLoading();
            doRequest(new GetPopularLocationQuery(type, constraint), this::onSearchSucceed);
        }
    }

    public void onSearchSucceed(List<PopularBucketItem> items) {
        if (view != null) {
            view.finishLoading();
            view.getAdapter().setFilteredItems(items);
        }
    }

    public void searchClosed() {
        view.getAdapter().flushFilter();
    }

    public void onAdd(PopularBucketItem popularBucketItem, int position) {
        add(popularBucketItem, false, position);
    }

    public void onDone(PopularBucketItem popularBucketItem, int position) {
        add(popularBucketItem, true, position);
    }

    private void add(PopularBucketItem popularBucketItem, boolean done, int position) {
        view.bind(bucketInteractor.createPipe()
                .createObservableResult(new CreateBucketItemHttpAction(ImmutableBucketBodyImpl.builder()
                        .type(type.getName())
                        .id(String.valueOf(popularBucketItem.getId()))
                        .status(done ? COMPLETED : NEW)
                        .build()))
                .map(CreateBucketItemHttpAction::getResponse)
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(bucketItem -> {
                    sweetDialogHelper.notifyItemAddedToBucket(activity, bucketItem);
                    view.getAdapter().remove(popularBucketItem);
                }, throwable -> {
                    handleError(throwable);

                    popularBucketItem.setLoading(false);
                    view.getAdapter().notifyDataSetChanged();
                });
    }

    public void reload() {
        view.startLoading();
        doRequest(new GetPopularLocation(type), items -> {
            view.finishLoading();
            //
            view.getAdapter().clear();
            view.getAdapter().addItems(items);
            view.getAdapter().notifyDataSetChanged();
        });
    }

    @Override
    public void handleError(SpiceException error) {
        view.finishLoading();
        super.handleError(error);
    }

    public interface View extends RxView {
        FilterableArrayListAdapter<PopularBucketItem> getAdapter();

        void startLoading();

        void finishLoading();
    }
}