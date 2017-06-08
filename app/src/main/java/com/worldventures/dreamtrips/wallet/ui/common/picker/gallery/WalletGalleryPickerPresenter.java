package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;


import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerPresenter;

public interface WalletGalleryPickerPresenter extends BaseWalletPickerPresenter<WalletGalleryPickerView> {

   void tryOpenCamera();

   void openCamera();

   void attachImages();
}
