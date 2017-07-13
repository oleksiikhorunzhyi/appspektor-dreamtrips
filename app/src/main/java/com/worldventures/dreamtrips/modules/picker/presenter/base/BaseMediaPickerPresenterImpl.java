package com.worldventures.dreamtrips.modules.picker.presenter.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.worldventures.dreamtrips.modules.picker.model.BaseMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.view.base.BaseMediaPickerView;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public abstract class BaseMediaPickerPresenterImpl<V extends BaseMediaPickerView, M extends BaseMediaPickerViewModel> extends MvpBasePresenter<V> implements BaseMediaPickerPresenter<V, M> {

   private final PublishSubject<List<M>> resultPublishSubject = PublishSubject.create();

   public void attachView(V view){
      super.attachView(view);
   }

   @Override
   public void loadItems() {

   }

   @Override
   public void performCleanup() {
      getView().clearItems();
   }

   @Override
   public Observable<List<M>> attachedItems() {
      return resultPublishSubject.asObservable().startWith(Observable.just(Collections.emptyList()));
   }

   public PublishSubject<List<M>> getResultPublishSubject() {
      return resultPublishSubject;
   }
}
