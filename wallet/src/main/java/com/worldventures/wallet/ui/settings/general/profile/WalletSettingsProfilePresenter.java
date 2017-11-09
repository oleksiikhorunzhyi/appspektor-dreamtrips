package com.worldventures.wallet.ui.settings.general.profile;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.wallet.ui.common.base.WalletPresenter;

public interface WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfileScreen> {

   void openDisplaySettings();

   void handleDoneAction();

   boolean isDataChanged();

   void handlePickedPhoto(PhotoPickerModel photoPickerModel);

   void choosePhoto();

   void doNotAdd();

   void goBack();

   void confirmDisplayTypeChange();

   void handleBackOnDataChanged();

   void revertChanges();
}
