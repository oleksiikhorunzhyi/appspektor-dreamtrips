package com.worldventures.wallet.ui.settings.general.display.impl

import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand
import com.worldventures.wallet.service.command.settings.general.display.SaveDisplayTypeCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsScreen
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate
import com.worldventures.wallet.util.GuaranteedProgressVisibilityTransformer
import com.worldventures.wallet.util.WalletFilesUtils
import io.techery.janet.ActionState
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import timber.log.Timber

class DisplayOptionsSettingsPresenterImpl(navigator: Navigator,
                                          deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                          private val delegate: WalletProfileDelegate,
                                          private val smartCardInteractor: SmartCardInteractor,
                                          private val socialInfoProvider: WalletSocialInfoProvider)
   : WalletPresenterImpl<DisplayOptionsSettingsScreen>(navigator, deviceConnectionDelegate), DisplayOptionsSettingsPresenter {

   override fun attachView(view: DisplayOptionsSettingsScreen) {
      super.attachView(view)

      observeHomeDisplay()
      if (!view.isProfileBind) {
         observeUserAndFetchDisplayType(view)
      }
      observeUserProfileUploading()
   }

   private fun observeUserAndFetchDisplayType(view: DisplayOptionsSettingsScreen) {
      Observable.combineLatest(
            smartCardInteractor.smartCardUserPipe()
                  .createObservableResult(SmartCardUserCommand.fetch())
                  .map { delegate.toViewModel(it.result) },
            smartCardInteractor.displayTypePipe
                  .createObservableResult(GetDisplayTypeCommand(true))
                  .map { it.result },
            { first, second -> Pair(first, second) })
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .subscribe({ pair -> view.setupDisplayOptions(pair.first, pair.second) }) { t -> Timber.e(t) }
   }

   private fun observeHomeDisplay() {
      smartCardInteractor.displayTypePipe.observe()
            .compose(GuaranteedProgressVisibilityTransformer<ActionState<GetDisplayTypeCommand>>())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideGetDisplayTypeOperationView())
                  .create()
            )
      smartCardInteractor.saveDisplayTypePipe().observe()
            .compose(GuaranteedProgressVisibilityTransformer<ActionState<SaveDisplayTypeCommand>>())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideSaveDisplayTypeOperationView())
                  .onSuccess {
                     if (!view.isProfileChanged) {
                        proceedWithChangesApply()
                     }
                  }
                  .create()
            )
   }

   private fun observeUserProfileUploading() {
      delegate.observeProfileUploading(view, { proceedWithChangesApply() }, null)
   }

   override fun phoneNumberEntered(phoneCode: String, phoneNumber: String) {
      if (delegate.isPhoneValid(phoneCode, phoneNumber)) {
         view.updatePhone(phoneCode, phoneNumber)
      }
   }

   override fun handlePickedPhoto(model: PhotoPickerModel) {
      view.cropPhoto(WalletFilesUtils.convertPickedPhotoToUri(model))
   }

   override fun avatarSelected(imageUri: String) {
      view.updatePhoto(imageUri)
   }

   override fun choosePhoto() {
      view.pickPhoto(delegate.provideInitialPhotoUrl(socialInfoProvider.photoThumb()))
   }

   override fun saveDisplayType(@SetHomeDisplayTypeAction.HomeDisplayType type: Int) {
      val saveDisplayType = SaveDisplayTypeCommand(type, delegate.createSmartCardUser(view.profile),
            if (view.isProfileChanged) Action1 { delegate.updateUser(it, false) } else null)
      smartCardInteractor.saveDisplayTypePipe().send(saveDisplayType)
   }

   override fun goBack() {
      navigator.goBack()
   }

   override fun fetchDisplayType() {
      smartCardInteractor.displayTypePipe.send(GetDisplayTypeCommand(true))
   }

   private fun proceedWithChangesApply() {
      if (view.displayOptionsSource.isSettings) {
         goBack()
      } else {
         navigator.goBackToProfile()
      }
   }
}
