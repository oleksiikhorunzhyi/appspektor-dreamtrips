package com.worldventures.wallet.ui.settings.general.display;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.wallet.ui.common.base.WalletPresenter;

import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

public interface DisplayOptionsSettingsPresenter extends WalletPresenter<DisplayOptionsSettingsScreen> {

   void goBack();

   void saveDisplayType(@SetHomeDisplayTypeAction.HomeDisplayType int type);

   void fetchDisplayType();

   void phoneNumberEntered(String phoneCode, String phoneNumber);

   void handlePickedPhoto(PhotoPickerModel photoPickerModel);

   void avatarSelected(String imageUri);

   void choosePhoto();
}