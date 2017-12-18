package com.worldventures.wallet.ui.settings.general.profile

import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.wallet.ui.common.base.WalletPresenter

interface WalletSettingsProfilePresenter : WalletPresenter<WalletSettingsProfileScreen> {

   fun openDisplaySettings()

   fun handleDoneAction()

   fun handlePickedPhoto(photoPickerModel: PhotoPickerModel)

   fun choosePhoto()

   fun doNotAdd()

   fun goBack()

   fun confirmDisplayTypeChange()

   fun handleBackOnDataChanged()

   fun revertChanges()
}
