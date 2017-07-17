package com.worldventures.dreamtrips.modules.picker.presenter.facebook;

import com.worldventures.dreamtrips.modules.facebook.service.FacebookInteractor;
import com.worldventures.dreamtrips.modules.picker.model.FacebookMediaPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.presenter.base.BaseMediaPickerPresenterImpl;
import com.worldventures.dreamtrips.modules.picker.view.facebook.FacebookMediaPickerView;


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
