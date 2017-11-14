package com.worldventures.wallet.ui.settings.general.display;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.wallet.ui.common.base.WalletPresenter;
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel;

import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;

public interface DisplayOptionsSettingsPresenter extends WalletPresenter<DisplayOptionsSettingsScreen> {

   void goBack();

   void saveDisplayType(@SetHomeDisplayTypeAction.HomeDisplayType int type);

   void fetchDisplayType();

   void savePhoneNumber(ProfileViewModel profile);

   void handlePickedPhoto(PhotoPickerModel photoPickerModel);

   void saveAvatar(String imageUri);

   void choosePhoto();
}