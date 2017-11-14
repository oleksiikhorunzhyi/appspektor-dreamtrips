package com.worldventures.core.modules.picker.presenter.facebook;


import com.worldventures.core.modules.picker.presenter.base.BaseMediaPickerPresenter;
import com.worldventures.core.modules.picker.view.facebook.FacebookMediaPickerView;
import com.worldventures.core.modules.picker.viewmodel.FacebookMediaPickerViewModel;

public interface FacebookMediaPickerPresenter<V extends FacebookMediaPickerView, M extends FacebookMediaPickerViewModel> extends BaseMediaPickerPresenter<V, M> {

   void loadMore();

}
