package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

public interface WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfileScreen> {

   void openDisplaySettings();

   void handleDoneAction();

   boolean isDataChanged();

   void handlePickedPhoto(PhotoPickerModel photoPickerModel);

   void choosePhoto();

   void doNotAdd();

   void goBack();

   void retryUploadToServer();

   void cancelUploadServerUserData();

   void confirmDisplayTypeChange();

   void handleBackOnDataChanged();
}
