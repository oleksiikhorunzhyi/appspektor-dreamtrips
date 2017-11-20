package com.worldventures.wallet.ui.settings.general.profile.impl

import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.wallet.analytics.settings.SmartCardProfileAction
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardUserDataInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.device.DeviceStateCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSource
import com.worldventures.wallet.ui.settings.general.profile.WalletSettingsProfilePresenter
import com.worldventures.wallet.ui.settings.general.profile.WalletSettingsProfileScreen
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate
import com.worldventures.wallet.util.FirstNameException
import com.worldventures.wallet.util.FormatException
import com.worldventures.wallet.util.LastNameException
import com.worldventures.wallet.util.MiddleNameException
import com.worldventures.wallet.util.WalletFilesUtils
import com.worldventures.wallet.util.WalletValidateHelper
import io.techery.janet.helper.ActionStateSubscriber
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class WalletSettingsProfilePresenterImpl(navigator: Navigator,
                                         deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                         analyticsInteractor: WalletAnalyticsInteractor,
                                         private val smartCardInteractor: SmartCardInteractor,
                                         smartCardUserDataInteractor: SmartCardUserDataInteractor,
                                         private val socialInfoProvider: WalletSocialInfoProvider)
   : WalletPresenterImpl<WalletSettingsProfileScreen>(navigator, deviceConnectionDelegate), WalletSettingsProfilePresenter {

   private val delegate: WalletProfileDelegate = WalletProfileDelegate(smartCardUserDataInteractor, smartCardInteractor, analyticsInteractor)

   override fun attachView(view: WalletSettingsProfileScreen) {
      super.attachView(view)

      fetchProfile()
      observeChangeFields(view)

      delegate.observeProfileUploading(view, { this.applyChanges(it) }, { view.setDoneButtonEnabled(view.isDataChanged) })
      delegate.sendAnalytics(SmartCardProfileAction())

   }

   private fun observeChangeFields(view: WalletSettingsProfileScreen) {
      view.observeChangesProfileFields()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { view.setDoneButtonEnabled(view.isDataChanged) }
   }

   private fun fetchProfile() {
      smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ setUser(it.result) }) { Timber.e(it) }
   }

   private fun setUser(user: SmartCardUser) {
      if (view.profile.isEmpty) {
         view.profile = delegate.toViewModel(user)
      }
      view.setDoneButtonEnabled(view.isDataChanged)
   }

   override fun handleDoneAction() {
      assertSmartCardConnected { saveUserProfile(false) }
   }

   override fun confirmDisplayTypeChange() {
      assertSmartCardConnected { saveUserProfile(true) }
   }

   override fun handleBackOnDataChanged() {
      view.showRevertChangesDialog()
   }

   override fun revertChanges() {
      view.discardChanges()
      goBack()
   }

   private fun applyChanges(updatedUser: SmartCardUser) {
      view.profile = delegate.toViewModel(updatedUser)
      goBack()
   }

   private fun saveUserProfile(forceUpdateDisplayType: Boolean) {
      view.setDoneButtonEnabled(false)
      delegate.updateUser(view.profile, forceUpdateDisplayType)
   }

   override fun goBack() {
      navigator.goBack()
   }

   override fun choosePhoto() {
      view.pickPhoto(delegate.provideInitialPhotoUrl(socialInfoProvider.photoThumb()))
   }

   override fun doNotAdd() {
      view.dropPhoto()
   }

   override fun handlePickedPhoto(photoPickerModel: PhotoPickerModel) {
      view.cropPhoto(WalletFilesUtils.convertPickedPhotoToUri(photoPickerModel))
   }

   override fun openDisplaySettings() {
      assertSmartCardConnected {
         val profileViewModel = view.profile
         if (validateViewModel(profileViewModel) { view.provideUpdateSmartCardOperation(delegate).showError(null, it) }) {
            navigator.goSettingsDisplayOptions(DisplayOptionsSource.PROFILE, profileViewModel)
         }
      }
   }

   private fun assertSmartCardConnected(onConnected: () -> Unit) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess { command ->
                     if (command.result.connectionStatus.isConnected) {
                        onConnected.invoke()
                     } else {
                        view.showSCNonConnectionDialog()
                     }
                  }
            )
   }

   // util methods:
   private fun validateViewModel(viewModel: ProfileViewModel, errorCallback: (FormatException) -> Unit): Boolean {
      if (!WalletValidateHelper.isValidFirstName(viewModel.firstName)) {
         errorCallback.invoke(FirstNameException())
         return false
      }
      if (!WalletValidateHelper.isValidMiddleName(viewModel.middleName)) {
         errorCallback.invoke(MiddleNameException())
         return false
      }
      if (!WalletValidateHelper.isValidLastName(viewModel.lastName)) {
         errorCallback.invoke(LastNameException())
         return false
      }
      return true
   }

}
