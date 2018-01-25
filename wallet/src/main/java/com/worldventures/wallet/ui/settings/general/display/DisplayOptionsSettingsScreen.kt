package com.worldventures.wallet.ui.settings.general.display

import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand
import com.worldventures.wallet.service.command.settings.general.display.SaveDisplayTypeCommand
import com.worldventures.wallet.ui.common.base.screen.LifecycleHolder
import com.worldventures.wallet.ui.common.base.screen.WalletScreen
import com.worldventures.wallet.ui.settings.general.display.impl.DisplayOptionsSource
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel
import com.worldventures.wallet.ui.settings.general.profile.common.UpdateSmartCardUserView
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfilePhotoView
import io.techery.janet.operationsubscriber.view.OperationView
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction

interface DisplayOptionsSettingsScreen : WalletScreen, WalletProfilePhotoView, UpdateSmartCardUserView, LifecycleHolder {

   val displayOptionsSource: DisplayOptionsSource

   val isProfileChanged: Boolean

   val isProfileBind: Boolean

   val profile: ProfileViewModel

   fun setupDisplayOptions(displayModel: ProfileViewModel, @SetHomeDisplayTypeAction.HomeDisplayType type: Int)

   fun provideGetDisplayTypeOperationView(): OperationView<GetDisplayTypeCommand>

   fun provideSaveDisplayTypeOperationView(): OperationView<SaveDisplayTypeCommand>

   fun showAddPhoneDialog()

   fun updatePhone(phoneCode: String, phoneNumber: String)

   fun updatePhoto(photo: String)
}
