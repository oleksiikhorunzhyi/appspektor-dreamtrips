package com.worldventures.wallet.ui.settings.general.profile.common

import com.worldventures.core.janet.composer.ActionPipeCacheWiper
import com.worldventures.wallet.analytics.WalletAnalyticsAction
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.settings.ProfileChangesSavedAction
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.SmartCardUserPhone
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.service.SmartCardUserDataInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.profile.RetryHttpUploadUpdatingCommand
import com.worldventures.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1

class WalletProfileDelegate(private val smartCardUserDataInteractor: SmartCardUserDataInteractor,
                            private val analyticsInteractor: WalletAnalyticsInteractor) {

   @JvmOverloads
   fun observeProfileUploading(view: UpdateSmartCardUserView,
                               onSuccess: Action1<SmartCardUser>? = null, onFailure: Action1<Throwable>? = null) {

      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ActionPipeCacheWiper(smartCardUserDataInteractor.updateSmartCardUserPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideUpdateSmartCardOperation(this))
                  .onSuccess { setupUserDataCommand ->
                     sendAnalytics(ProfileChangesSavedAction())
                     onSuccess?.call(setupUserDataCommand.result)
                  }
                  .onFail { _, throwable ->
                     onFailure?.call(throwable)
                  }
                  .create())

      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ActionPipeCacheWiper(smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideHttpUploadOperation(this)).create())
   }

   fun cancelUploadServerUserData() {
      smartCardUserDataInteractor.revertSmartCardUserUpdatingPipe().send(RevertSmartCardUserUpdatingCommand())
   }

   fun retryUploadToServer() {
      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe().send(RetryHttpUploadUpdatingCommand())
   }

   fun sendAnalytics(action: WalletAnalyticsAction) {
      val analyticsCommand = WalletAnalyticsCommand(action)
      analyticsInteractor.walletAnalyticsPipe().send(analyticsCommand)
   }

   @Deprecated("This method does nothing")
   fun observePickerAndCropper(view: WalletProfilePhotoView) {
      //      view.observeCropper().compose(view.lifecycle()).subscribe(photoFile -> { /*nothing*/ });
   }

   fun createPhone(model: ProfileViewModel): SmartCardUserPhone? {
      return if (model.phoneCode.isNullOrEmpty() || model.phoneNumber.isNullOrEmpty()) {
         null
      } else {
         SmartCardUserPhone("+${model.phoneCode}", model.phoneNumber)
      }
   }

   fun createPhoto(model: ProfileViewModel): SmartCardUserPhoto? {
      return if (!model.isPhotoEmpty) SmartCardUserPhoto(model.chosenPhotoUri!!) else null
   }

   fun toViewModel(user: SmartCardUser): ProfileViewModel {
      val phone = user.phoneNumber
      val photo = user.userPhoto
      val model = ProfileViewModel()

      model.firstName = user.firstName
      model.middleName = user.middleName
      model.lastName = user.lastName

      if (phone != null) {
         model.phoneCode = phone.code.replace("+", "")
         model.phoneNumber = phone.number
      }
      model.chosenPhotoUri = photo?.uri
      return model
   }

   fun toViewModel(firstName: String, lastName: String, photoUri: String?): ProfileViewModel {
      val model = ProfileViewModel()
      model.firstName = firstName
      model.lastName = lastName
      if (photoUri != null) {
         model.chosenPhotoUri = photoUri
      }
      return model
   }

   fun createSmartCardUser(profile: ProfileViewModel): SmartCardUser {
      return SmartCardUser(
            firstName = profile.firstName,
            middleName = profile.middleName,
            lastName = profile.lastNameWithSuffix,
            phoneNumber = createPhone(profile),
            userPhoto = createPhoto(profile))
   }

   fun provideInitialPhotoUrl(userPhotoUrl: String?): String? {
      return if (userPhotoUrl != null && !WalletProfileUtils.isPhotoEmpty(userPhotoUrl)) userPhotoUrl else null
   }
}
