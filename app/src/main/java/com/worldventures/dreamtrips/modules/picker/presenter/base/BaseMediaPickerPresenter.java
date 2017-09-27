package com.worldventures.dreamtrips.modules.picker.presenter.base;

import com.worldventures.dreamtrips.modules.picker.model.BaseMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.view.base.BaseMediaPickerView;

import java.util.List;

import rx.Observable;

public interface BaseMediaPickerPresenter<V extends BaseMediaPickerView, M extends BaseMediaPickerViewModel> {

   void attachView(V view);

   void detachView(boolean retainInstance);

   void loadItems();

   void performCleanup();

   Observable<List<M>> attachedItems();
}
