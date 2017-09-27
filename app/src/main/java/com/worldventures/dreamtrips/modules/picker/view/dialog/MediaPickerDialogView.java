package com.worldventures.dreamtrips.modules.picker.view.dialog;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.dreamtrips.modules.picker.model.BaseMediaPickerViewModel;

import java.util.List;

import rx.Observable;


public interface MediaPickerDialogView extends MvpView {

   void onDone();

   void updatePickedItemsCount(int count);

   boolean canGoBack();

   void goBack();

   Observable<List<BaseMediaPickerViewModel>> attachedMedia();

   int getPickLimit();

   int getRequestId();

   <T> Observable.Transformer<T, T> lifecycle();
}
