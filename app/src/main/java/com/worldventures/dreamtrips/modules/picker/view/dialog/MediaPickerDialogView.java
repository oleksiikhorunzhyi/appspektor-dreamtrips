package com.worldventures.dreamtrips.modules.picker.view.dialog;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;
import com.worldventures.dreamtrips.modules.picker.model.BaseMediaPickerViewModel;

import java.util.List;

import rx.Observable;


public interface MediaPickerDialogView extends MvpView, RxLifecycleView {

   void onDone();

   void updatePickedItemsCount(int count);

   boolean canGoBack();

   void goBack();

   Observable<List<BaseMediaPickerViewModel>> attachedMedia();

   int getPickLimit();

   int getRequestId();
}
