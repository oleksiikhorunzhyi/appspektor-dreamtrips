package com.worldventures.core.modules.picker.view.dialog;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.worldventures.core.modules.picker.viewmodel.BaseMediaPickerViewModel;

import java.util.List;

import rx.Observable;


public interface MediaPickerDialogView extends MvpView {

   void onDone();

   void updatePickedItemsCount(int count);

   boolean canGoBack();

   void goBack();

   Observable<List<BaseMediaPickerViewModel>> attachedMedia();

   int getPickLimit();

   int getVideoDurationLimit();

   int getRequestId();

   <T> Observable.Transformer<T, T> lifecycle();
}
