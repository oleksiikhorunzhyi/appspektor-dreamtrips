package com.worldventures.dreamtrips.wallet.ui.common.picker.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import rx.Observable;
import rx.subjects.PublishSubject;


public abstract class BaseWalletPickerPresenterImpl<V extends BaseWalletPickerView> extends MvpBasePresenter<V> implements BaseWalletPickerPresenter<V> {

   private final PublishSubject<WalletPickerAttachment> resultPublishSubject = PublishSubject.create();

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
   public Observable<WalletPickerAttachment> attachedPhotos() {
      return resultPublishSubject.asObservable();
   }

   public PublishSubject<WalletPickerAttachment> getResultPublishSubject() {
      return resultPublishSubject;
   }
}
