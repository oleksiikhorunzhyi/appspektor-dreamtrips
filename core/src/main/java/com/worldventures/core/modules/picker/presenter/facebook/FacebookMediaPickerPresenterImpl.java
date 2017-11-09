package com.worldventures.core.modules.picker.presenter.facebook;

import com.worldventures.core.modules.facebook.service.FacebookInteractor;
import com.worldventures.core.modules.picker.presenter.base.BaseMediaPickerPresenterImpl;
import com.worldventures.core.modules.picker.view.facebook.FacebookMediaPickerView;
import com.worldventures.core.modules.picker.viewmodel.FacebookMediaPickerViewModel;


public abstract class FacebookMediaPickerPresenterImpl<V extends FacebookMediaPickerView, M extends FacebookMediaPickerViewModel>
      extends BaseMediaPickerPresenterImpl<V, M> {

   private final FacebookInteractor facebookInteractor;

   public FacebookMediaPickerPresenterImpl(FacebookInteractor facebookInteractor) {
      this.facebookInteractor = facebookInteractor;
   }

   @Override
   public void attachView(V view) {
      super.attachView(view);
      observeItemSource();
   }

   public FacebookInteractor getFacebookInteractor() {
      return facebookInteractor;
   }

   public abstract void observeItemSource();
}
