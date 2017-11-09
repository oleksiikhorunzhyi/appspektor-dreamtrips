package com.worldventures.core.modules.picker.presenter.base;

import com.worldventures.core.modules.picker.view.base.BaseMediaPickerView;
import com.worldventures.core.modules.picker.viewmodel.BaseMediaPickerViewModel;

import java.util.List;

import rx.Observable;

public interface BaseMediaPickerPresenter<V extends BaseMediaPickerView, M extends BaseMediaPickerViewModel> {

   void attachView(V view);

   void detachView(boolean retainInstance);

   void loadItems();

   void performCleanup();

   Observable<List<M>> attachedItems();
}
