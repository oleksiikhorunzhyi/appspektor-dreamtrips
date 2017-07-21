package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterI;

public interface WalletSettingsProfilePresenter extends WalletPresenterI<WalletSettingsProfileScreen> {

   void openDisplaySettings();

   void handleDoneAction();

   void handleBackAction();

   void handlePickedPhoto(PhotoPickerModel photoPickerModel);

   void choosePhoto();

   void doNotAdd();

   void goBack();

   void retryUploadToServer();

   void cancelUploadServerUserData();

   void confirmDisplayTypeChange();
}
