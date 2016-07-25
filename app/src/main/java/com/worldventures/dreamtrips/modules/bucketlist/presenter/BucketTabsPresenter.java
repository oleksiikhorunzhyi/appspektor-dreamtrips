package com.worldventures.dreamtrips.modules.bucketlist.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.api.GetCategoryQuery;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemPhotoAnalyticEvent;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType.ACTIVITY;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType.DINING;
import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType.LOCATION;

public class BucketTabsPresenter extends Presenter<BucketTabsPresenter.View> {
    @Inject
    SnappyRepository db;

    @Inject
    BucketInteractor bucketInteractor;

    @Override
    public void takeView(View view) {
        super.takeView(view);
        setTabs();
        loadCategories();

        view.bind(bucketInteractor.bucketListActionPipe().createObservableResult(BucketListCommand.fetch(getUser().getId(),false))
                .concatMap(bucketListAction -> bucketListAction.isFromCache() ?
                        bucketInteractor.bucketListActionPipe().createObservable(BucketListCommand.fetch(getUser().getId(), true))
                        : Observable.just(bucketListAction))
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(bucketListAction -> {
                }, this::handleError);
    }

    @Override
    public void onResume() {
        super.onResume();

        view.bind(recentTabCountObservable()
                .map(Command::getResult))
                .subscribe(bucketTypeListPair -> {
                    view.setRecentBucketItemCountByType(bucketTypeListPair.first, bucketTypeListPair.second.size());
                });
    }

    @Override
    public void dropView() {
        super.dropView();
        db.saveOpenBucketTabType(null);
    }

    protected User getUser() {
        return getAccount();
    }

    private void loadCategories() {
        doRequest(new GetCategoryQuery(),
                categoryItems -> db.putList(SnappyRepository.CATEGORIES, categoryItems));
    }

    public void setTabs() {
        view.setTypes(Arrays.asList(LOCATION, ACTIVITY, DINING));
        view.updateSelection();
    }

    public void onTabChange(BucketType type) {
        bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe()
                .send(RecentlyAddedBucketsFromPopularCommand.clear(type));

        db.saveOpenBucketTabType(type.name());
    }

    public void onEvent(BucketAnalyticEvent event) {
        TrackingHelper.actionBucket(event.getActionAttribute(), getTabAttributeAnalytic());
    }

    public void onEvent(BucketItemAnalyticEvent event) {
        TrackingHelper.actionBucketItem(event.getActionAttribute(), event.getBucketItemId());
    }

    public void onEvent(BucketItemPhotoAnalyticEvent event) {
        TrackingHelper.actionBucketItemPhoto(event.getActionAttribute(), event.getBucketItemId());
    }

    private Observable<RecentlyAddedBucketsFromPopularCommand> recentTabCountObservable() {
        ActionPipe<RecentlyAddedBucketsFromPopularCommand> recentPipe = bucketInteractor.recentlyAddedBucketsFromPopularCommandPipe();
        return Observable.merge(recentPipe.createObservableResult(RecentlyAddedBucketsFromPopularCommand.get(LOCATION)),
                recentPipe.createObservableResult(RecentlyAddedBucketsFromPopularCommand.get(ACTIVITY)),
                recentPipe.createObservableResult(RecentlyAddedBucketsFromPopularCommand.get(DINING)));
    }

    private String getTabAttributeAnalytic() {
        String tabAttribute = "";
        switch (view.getCurrentTabPosition()) {
            case 0:
                tabAttribute = TrackingHelper.ATTRIBUTE_LOCATIONS;
                break;
            case 1:
                tabAttribute = TrackingHelper.ATTRIBUTE_ACTIVITIES;
                break;
            case 2:
                tabAttribute = TrackingHelper.ATTRIBUTE_DINING;
                break;
        }
        return tabAttribute;
    }

    public interface View extends RxView {
        void setTypes(List<BucketType> type);

        void setRecentBucketItemCountByType(BucketType type, int count);

        void updateSelection();

        int getCurrentTabPosition();
    }
}