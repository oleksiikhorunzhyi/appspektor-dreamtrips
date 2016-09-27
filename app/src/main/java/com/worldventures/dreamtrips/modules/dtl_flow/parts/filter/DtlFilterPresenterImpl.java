package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class DtlFilterPresenterImpl implements DtlFilterPresenter {

   @Inject FilterDataInteractor filterDataInteractor;
   @Inject AttributesInteractor attributesInteractor;

   private FilterView view;
   private PublishSubject<Void> detachStopper = PublishSubject.create();
   private PublishSubject<Void> hideStopper = PublishSubject.create();

   private void closeDrawer() {
      if (view != null) view.toggleDrawer(false);
   }

   @Override
   public void attachView(FilterView view) {
      this.view = view;
      this.view.getInjector().inject(this);
      bindFilterUpdates();
      bindAmenitiesUpdate();
   }

   @Override
   public void onDrawerOpened() {
      retryAmenitiesIfError();
   }

   @Override
   public void onDrawerClosed() {
      hideStopper.onNext(null);
   }

   @Override
   public void detachView(boolean retainInstance) {
      view = null;
      detachStopper.onNext(null);
   }

   @Override
   public void apply() {
      filterDataInteractor.mergeAndApply(view.getFilterData());
      closeDrawer();
   }

   @Override
   public void resetAll() {
      filterDataInteractor.reset();
      closeDrawer();
   }

   private void retryAmenitiesIfError() {
      attributesInteractor.attributesPipe()
            .observeWithReplay()
            .filter(attributesActionActionState -> attributesActionActionState.status == ActionState.Status.FAIL)
            .take(1)
            .takeUntil(hideStopper.asObservable())
            .takeUntil(detachStopper.asObservable())
            .subscribe(attributesActionActionState ->
                  retryAmenities());
   }

   @Override
   public void retryAmenities() {
      attributesInteractor.requestAmenities();
   }

   private void bindFilterUpdates() {
      filterDataInteractor.filterDataPipe().observeSuccessWithReplay()
            .map(FilterDataAction::getResult)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(detachStopper.asObservable())
            .subscribe(filterData -> view.applyFilterState(filterData));
   }

   private void bindAmenitiesUpdate() {
      attributesInteractor.attributesPipe()
            .observeWithReplay()
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(detachStopper.asObservable())
            .subscribe(new ActionStateSubscriber<AttributesAction>()
                  .onSuccess(attributesAction -> {
                     filterDataInteractor.filterDataPipe().observeSuccessWithReplay()
                           .take(1)
                           .map(FilterDataAction::getResult)
                           .observeOn(AndroidSchedulers.mainThread())
                           .takeUntil(detachStopper.asObservable())
                           .subscribe(filterData ->
                                 view.showAmenitiesItems(attributesAction.getResult(), filterData));
                  })
                  .onProgress((attributesAction, integer) -> view.showAmenitiesListProgress())
                  .onFail((attributesAction, throwable) -> view.showAmenitiesError()));
   }
}
