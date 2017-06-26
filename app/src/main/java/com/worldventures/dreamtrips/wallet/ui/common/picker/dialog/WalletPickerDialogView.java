package com.worldventures.dreamtrips.wallet.ui.common.picker.dialog;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerAttachment;

import rx.Observable;


public interface WalletPickerDialogView extends MvpView, RxLifecycleView {

   void onDone();

   void updatePickedItemsCount(int count);

   WalletPickerStep getCurrentStep();

   void goBack();

   Observable<WalletPickerAttachment> attachedPhotos();

   int getPickLimit();
}
