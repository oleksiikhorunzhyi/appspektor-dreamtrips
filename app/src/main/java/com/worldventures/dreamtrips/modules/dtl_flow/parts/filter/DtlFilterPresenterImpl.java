package com.worldventures.dreamtrips.modules.dtl_flow.parts.filter;

import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.AttributesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

public class DtlFilterPresenterImpl implements DtlFilterPresenter {

   @Inject DtlFilterMerchantInteractor filterInteractor;
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
      updateFilterState();
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
      filterInteractor.filterDataPipe().send(DtlFilterDataAction.applyParams(view.getFilterParameters()));
      closeDrawer();
   }

   @Override
   public void resetAll() {
      filterInteractor.filterDataPipe().send(DtlFilterDataAction.reset());
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
                  attributesInteractor.attributesPipe().send(new AttributesAction()));
   }

   @Override
   public void retryAmenities() {
      attributesInteractor.attributesPipe().send(new AttributesAction());
   }

   private void bindFilterUpdates() {
      filterInteractor.filterDataPipe().observeSuccess()
            .map(DtlFilterDataAction::getResult)
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(detachStopper.asObservable())
            .subscribe(view::syncUi);
   }

   private void updateFilterState() {
      filterInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlFilterDataAction::getResult)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::syncUi);
   }

   private void bindAmenitiesUpdate() {
      attributesInteractor.attributesPipe()
            .observeWithReplay()
            .observeOn(AndroidSchedulers.mainThread())
            .takeUntil(detachStopper.asObservable())
            .subscribe(new ActionStateSubscriber<AttributesAction>()
                  .onSuccess(attributesAction -> view.showAmenitiesItems(attributesAction.getResult()))
                  .onProgress((attributesAction, integer) -> view.showAmenitiesListProgress())
                  .onFail((attributesAction, throwable) -> view.showAmenitiesError()));
   }
}
