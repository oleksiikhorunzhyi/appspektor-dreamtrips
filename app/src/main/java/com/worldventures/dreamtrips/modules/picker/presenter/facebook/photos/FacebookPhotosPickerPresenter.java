package com.worldventures.dreamtrips.modules.picker.presenter.facebook.photos;


import com.worldventures.dreamtrips.modules.picker.model.FacebookPhotoPickerViewModel;
import com.worldventures.dreamtrips.modules.picker.presenter.facebook.FacebookMediaPickerPresenter;
import com.worldventures.dreamtrips.modules.picker.view.facebook.photos.FacebookPhotosPickerView;

public interface FacebookPhotosPickerPresenter extends FacebookMediaPickerPresenter<FacebookPhotosPickerView, FacebookPhotoPickerViewModel> {
   void attachImages();
}
