package com.worldventures.wallet.ui.wizard.profile

import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.wallet.ui.common.base.WalletPresenter

interface WizardEditProfilePresenter : WalletPresenter<WizardEditProfileScreen> {

   fun setupUserData()

   fun back()

   fun handlePickedPhoto(photoPickerModel: PhotoPickerModel)

   fun doNotAdd()

   fun choosePhoto()

   fun onUserDataConfirmed()
}
