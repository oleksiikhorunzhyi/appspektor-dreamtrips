package com.worldventures.dreamtrips.modules.picker.presenter.facebook;


import com.worldventures.dreamtrips.modules.picker.model.FacebookMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.presenter.base.BaseMediaPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.view.facebook.FacebookMediaPickerView;

public interface FacebookMediaPickerPresenter<V extends FacebookMediaPickerView, M extends FacebookMediaPickerViewModel> extends BaseMediaPickerPresenter<V, M> {

   void loadMore();

}
