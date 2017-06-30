package com.worldventures.dreamtrips.wallet.ui.common.picker.dialog;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;

import java.util.List;

import rx.Observable;


public interface WalletPickerDialogView extends MvpView, RxLifecycleView {

   void onDone();

   void updatePickedItemsCount(int count);

   boolean canGoBack();

   void goBack();

   Observable<List<BasePickerViewModel>> attachedMedia();

   int getPickLimit();

   int getRequestId();
}
