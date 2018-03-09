package com.worldventures.wallet.ui.wizard.profile.impl

import com.worldventures.core.janet.composer.ActionPipeCacheWiper
import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.wizard.PhotoWasSetAction
import com.worldventures.wallet.analytics.wizard.SetupUserAction
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardUserDataInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.WizardInteractor
import com.worldventures.wallet.service.command.SetupUserDataCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileUtils
import com.worldventures.wallet.ui.wizard.profile.WizardEditProfilePresenter
import com.worldventures.wallet.ui.wizard.profile.WizardEditProfileScreen
import com.worldventures.wallet.util.WalletFilesUtils
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import io.techery.janet.smartcard.action.user.RemoveUserPhotoAction
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action0
import rx.functions.Action1

class WizardEditProfilePresenterImpl(navigator: Navigator,
                                     deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                     private val smartCardInteractor: SmartCardInteractor,
                                     private val wizardInteractor: WizardInteractor,
                                     private val analyticsInteractor: WalletAnalyticsInteractor,
                                     private val socialInfoProvider: WalletSocialInfoProvider,
                                     smartCardUserDataInteractor: SmartCardUserDataInteractor)
   : WalletPresenterImpl<WizardEditProfileScreen>(navigator, deviceConnectionDelegate), WizardEditProfilePresenter {

   private val delegate: WalletProfileDelegate = WalletProfileDelegate(smartCardUserDataInteractor,
         smartCardInteractor, analyticsInteractor)

   override fun attachView(view: WizardEditProfileScreen) {
      super.attachView(view)
      if (getView().profile.isEmpty) {
         attachProfile(getView())
      }
      observeSetupUserCommand()

      delegate.sendAnalytics(SetupUserAction())
   }

   private fun observeSetupUserCommand() {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ActionPipeCacheWiper(wizardInteractor.setupUserDataPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideOperationView())
                  .onSuccess { onUserSetupSuccess(it.result) }
                  .create())
   }

   private fun onUserSetupSuccess(user: SmartCardUser) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(WalletAnalyticsCommand(
                  if (user.userPhoto != null) PhotoWasSetAction.methodDefault() else PhotoWasSetAction.noPhoto()))
      view.provisionMode.let { navigator.goWizardAssignUser(view.provisionMode) }
   }

   private fun attachProfile(view: WizardEditProfileScreen) {
      view.profile = delegate.toViewModel(
            socialInfoProvider.firstName() ?: "",
            socialInfoProvider.lastName() ?: "",
            socialInfoProvider.photoThumb()
      )
   }

   override fun back() = navigator.goBack()

   override fun choosePhoto() = view.pickPhoto(delegate.provideInitialPhotoUrl(socialInfoProvider.photoThumb()))

   override fun setupUserData() {
      val view = view

      val profile = view.profile

      WalletProfileUtils.checkUserNameValidation(profile.firstName, profile.middleName, profile.lastName,
            Action0 { view.showConfirmationDialog(profile) },
            Action1 { view.provideOperationView().showError(null, it) })
   }

   override fun onUserDataConfirmed() {
      val profile = view.profile
      val smartCardUser = delegate.createSmartCardUser(profile)
      wizardInteractor.setupUserDataPipe().send(SetupUserDataCommand(smartCardUser))
      if (profile.isPhotoEmpty) {
         smartCardInteractor.removeUserPhotoActionPipe()
               .send(RemoveUserPhotoAction())
      }
   }

   override fun doNotAdd() = view.dropPhoto()

   override fun handlePickedPhoto(model: PhotoPickerModel) = view.cropPhoto(WalletFilesUtils.convertPickedPhotoToUri(model))
}
