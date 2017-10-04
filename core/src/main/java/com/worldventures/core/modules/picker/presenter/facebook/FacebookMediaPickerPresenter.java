package com.worldventures.core.modules.picker.presenter.facebook;


import com.worldventures.core.modules.picker.viewmodel.FacebookMediaPickerViewModel;
import com.worldventures.core.modules.picker.presenter.base.BaseMediaPickerPresenter;
import com.worldventures.core.modules.picker.view.facebook.FacebookMediaPickerView;

public interface FacebookMediaPickerPresenter<V extends FacebookMediaPickerView, M extends FacebookMediaPickerViewModel> extends BaseMediaPickerPresenter<V, M> {

   void loadMore();

}
