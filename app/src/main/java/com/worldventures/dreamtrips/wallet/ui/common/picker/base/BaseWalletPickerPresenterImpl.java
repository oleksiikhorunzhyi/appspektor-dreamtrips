package com.worldventures.dreamtrips.wallet.ui.common.picker.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public abstract class BaseWalletPickerPresenterImpl<V extends BaseWalletPickerView> extends MvpBasePresenter<V> implements BaseWalletPickerPresenter<V> {

   private final PublishSubject<List<BasePickerViewModel>> resultPublishSubject = PublishSubject.create();

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
   public Observable<List<BasePickerViewModel>> attachedItems() {
      return resultPublishSubject.asObservable();
   }

   public PublishSubject<List<BasePickerViewModel>> getResultPublishSubject() {
      return resultPublishSubject;
   }
}
