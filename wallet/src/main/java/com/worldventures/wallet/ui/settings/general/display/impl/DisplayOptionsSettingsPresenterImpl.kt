package com.worldventures.wallet.ui.settings.general.display.impl

import com.worldventures.core.modules.picker.model.PhotoPickerModel
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardUserDataInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand
import com.worldventures.wallet.service.command.settings.general.display.SaveDisplayTypeCommand
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl
import com.worldventures.wallet.ui.common.navigation.Navigator
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsScreen
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate
import com.worldventures.wallet.util.GuaranteedProgressVisibilityTransformer
import com.worldventures.wallet.util.WalletFilesUtils
import io.techery.janet.ActionState
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class DisplayOptionsSettingsPresenterImpl(navigator: Navigator,
                                          deviceConnectionDelegate: WalletDeviceConnectionDelegate,
                                          private val smartCardInteractor: SmartCardInteractor,
                                          smartCardUserDataInteractor: SmartCardUserDataInteractor,
                                          analyticsInteractor: WalletAnalyticsInteractor,
                                          private val socialInfoProvider: WalletSocialInfoProvider)
   : WalletPresenterImpl<DisplayOptionsSettingsScreen>(navigator, deviceConnectionDelegate), DisplayOptionsSettingsPresenter {

   private val delegate: WalletProfileDelegate = WalletProfileDelegate(smartCardUserDataInteractor, smartCardInteractor, analyticsInteractor)

   private var mustSaveUserProfile: Boolean = false
   private var user: SmartCardUser? = null

   private val userObservable: Observable<SmartCardUser>
      get() = if (user != null)
         Observable.just(user)
      else
         smartCardInteractor.smartCardUserPipe()
               .createObservableResult(SmartCardUserCommand.fetch())
               .map { it.result }
               .doOnNext { smartCardUser -> user = smartCardUser }

   override fun attachView(view: DisplayOptionsSettingsScreen) {
      super.attachView(view)
      initiateData()
      observeHomeDisplay()
      observeUserProfileUploading()
      fetchDisplayType()
   }

   private fun initiateData() {
      val profileViewModel = view.profileViewModel
      this.user = if (profileViewModel != null) delegate.createSmartCardUser(profileViewModel) else null
      this.mustSaveUserProfile = user != null
   }

   private fun observeHomeDisplay() {
      Observable.combineLatest<SmartCardUser, Int, Pair<SmartCardUser, Int>>(
            userObservable,
            smartCardInteractor.displayTypePipe.observeSuccess()
                  .map { it.result },
            { first, second -> Pair(first, second) })
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .doOnSubscribe { this.fetchDisplayType() }
            .subscribe({ pair -> view.setupViewPager(pair.first, pair.second) }) { t -> Timber.e(t) }

      val getDisplayTypeOperationView = view.provideGetDisplayTypeOperationView()
      getDisplayTypeOperationView.showProgress(null)
      smartCardInteractor.displayTypePipe.observe()
            .compose(GuaranteedProgressVisibilityTransformer<ActionState<GetDisplayTypeCommand>>())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getDisplayTypeOperationView)
                  .create()
            )
      smartCardInteractor.saveDisplayTypePipe().observe()
            .compose(GuaranteedProgressVisibilityTransformer<ActionState<SaveDisplayTypeCommand>>())
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideSaveDisplayTypeOperationView())
                  .onSuccess {
                     if (view.displayOptionsSource.isSettings) {
                        goBack()
                     } else {
                        navigator.goBackToProfile()
                     }
                  }
                  .create()
            )
   }

   private fun observeUserProfileUploading() {
      delegate.observeProfileUploading(view)
   }

   override fun fetchDisplayType() {
      smartCardInteractor.displayTypePipe.send(GetDisplayTypeCommand(true))
   }

   override fun savePhoneNumber(profile: ProfileViewModel) {
      val enteredPhone = delegate.createPhone(profile) ?: return
      user = user?.copy(phoneNumber = enteredPhone)

      mustSaveUserProfile = true
      view.updateUser(user)
   }

   override fun handlePickedPhoto(model: PhotoPickerModel) {
      view.cropPhoto(WalletFilesUtils.convertPickedPhotoToUri(model))
   }

   override fun saveAvatar(imageUri: String) {
      user = user?.copy(userPhoto = SmartCardUserPhoto(imageUri))

      mustSaveUserProfile = true
      view.updateUser(user)
   }

   override fun choosePhoto() {
      view.pickPhoto(delegate.provideInitialPhotoUrl(socialInfoProvider.photoThumb()))
   }

   override fun saveDisplayType(@SetHomeDisplayTypeAction.HomeDisplayType type: Int) {
      user?.let {
         val saveDisplayType = SaveDisplayTypeCommand(type, it)
         if (mustSaveUserProfile) {
            updateProfileAndSaveDisplayType(it, saveDisplayType)
         } else {
            smartCardInteractor.saveDisplayTypePipe().send(saveDisplayType)
         }
      }
   }

   private fun updateProfileAndSaveDisplayType(user: SmartCardUser, saveDisplayType: SaveDisplayTypeCommand) {
      delegate.updateUser(user, true) { smartCardInteractor.saveDisplayTypePipe().send(saveDisplayType) }
   }

   override fun goBack() {
      navigator.goBack()
   }
}
