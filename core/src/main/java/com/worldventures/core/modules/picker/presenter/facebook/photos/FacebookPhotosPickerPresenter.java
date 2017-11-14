package com.worldventures.core.modules.picker.presenter.facebook.photos;


import com.worldventures.core.modules.picker.presenter.facebook.FacebookMediaPickerPresenter;
import com.worldventures.core.modules.picker.view.facebook.photos.FacebookPhotosPickerView;
import com.worldventures.core.modules.picker.viewmodel.FacebookPhotoPickerViewModel;

public interface FacebookPhotosPickerPresenter extends FacebookMediaPickerPresenter<FacebookPhotosPickerView, FacebookPhotoPickerViewModel> {
   void attachImages();
}
