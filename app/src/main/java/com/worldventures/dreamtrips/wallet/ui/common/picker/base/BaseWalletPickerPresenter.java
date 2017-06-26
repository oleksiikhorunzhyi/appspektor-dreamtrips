package com.worldventures.dreamtrips.wallet.ui.common.picker.base;


import rx.Observable;

public interface BaseWalletPickerPresenter<V extends BaseWalletPickerView> {

   void attachView(V view);

   void detachView(boolean retainInstance);

   void loadItems();

   void performCleanup();

   Observable<WalletPickerAttachment> attachedPhotos();
}
