package com.worldventures.dreamtrips.wallet.ui.common.picker.base;


import java.util.List;

import rx.Observable;

public interface BaseWalletPickerPresenter<V extends BaseWalletPickerView> {

   void attachView(V view);

   void detachView(boolean retainInstance);

   void loadItems();

   void performCleanup();

   Observable<List<BasePickerViewModel>> attachedItems();
}
