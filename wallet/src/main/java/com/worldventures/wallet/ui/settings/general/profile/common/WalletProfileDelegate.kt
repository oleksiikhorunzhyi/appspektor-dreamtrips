package com.worldventures.wallet.ui.settings.general.profile.common

import com.worldventures.core.janet.composer.ActionPipeCacheWiper
import com.worldventures.wallet.analytics.WalletAnalyticsAction
import com.worldventures.wallet.analytics.WalletAnalyticsCommand
import com.worldventures.wallet.analytics.settings.ProfileChangesSavedAction
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.SmartCardUserPhone
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.SmartCardUserDataInteractor
import com.worldventures.wallet.service.WalletAnalyticsInteractor
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import com.worldventures.wallet.service.profile.RetryHttpUploadUpdatingCommand
import com.worldventures.wallet.service.profile.RevertSmartCardUserUpdatingCommand
import com.worldventures.wallet.service.profile.UpdateSmartCardUserCommand
import io.techery.janet.operationsubscriber.OperationActionSubscriber
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

@Suppress("UnsafeCallOnNullableType")
class WalletProfileDelegate(private val smartCardUserDataInteractor: SmartCardUserDataInteractor,
                            private val smartCardInteractor: SmartCardInteractor,
                            private val analyticsInteractor: WalletAnalyticsInteractor) {

   fun observeProfileUploading(view: UpdateSmartCardUserView,
                               onSuccess: ((SmartCardUser) -> Unit)? = null, onFailure: ((Throwable) -> Unit)? = null) {

      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ActionPipeCacheWiper(smartCardUserDataInteractor.updateSmartCardUserPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideUpdateSmartCardOperation(this))
                  .onSuccess { setupUserDataCommand ->
                     sendAnalytics(ProfileChangesSavedAction())
                     onSuccess?.invoke(setupUserDataCommand.result)
                  }
                  .onFail { _, throwable ->
                     onFailure?.invoke(throwable)
                  }
                  .create())

      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(ActionPipeCacheWiper(smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideHttpUploadOperation(this)).create())
   }

   fun updateUser(profile: ProfileViewModel, forceUpdateDisplayType: Boolean) {
      updateUser(createSmartCardUser(profile), forceUpdateDisplayType)
   }

   fun updateUser(newUser: SmartCardUser, forceUpdateDisplayType: Boolean) {
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(ActiveSmartCardCommand())
            .flatMap { smartCardUserDataInteractor.updateSmartCardUserPipe()
                  .createObservableResult(UpdateSmartCardUserCommand(newUser, it.result.smartCardId, forceUpdateDisplayType)) }
            .subscribe({ }, { Timber.e(it) })
   }

   fun cancelUploadServerUserData(smartCardId: String, newUser: SmartCardUser) {
      smartCardUserDataInteractor.revertSmartCardUserUpdatingPipe()
            .send(RevertSmartCardUserUpdatingCommand(smartCardId, newUser))
   }

   fun retryUploadToServer(smartCardId: String, newUser: SmartCardUser) {
      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()
            .send(RetryHttpUploadUpdatingCommand(smartCardId, newUser))
   }

   fun sendAnalytics(action: WalletAnalyticsAction) {
      val analyticsCommand = WalletAnalyticsCommand(action)
      analyticsInteractor.walletAnalyticsPipe().send(analyticsCommand)
   }

   private fun createPhone(model: ProfileViewModel): SmartCardUserPhone? = createPhone(model.phoneCode, model.phoneNumber)

   private fun createPhone(phoneCode: String, phoneNumber: String): SmartCardUserPhone? {
      return if (!isPhoneValid(phoneCode, phoneNumber)) {
         null
      } else {
         SmartCardUserPhone("+$phoneCode", phoneNumber)
      }
   }

   fun isPhoneValid(phoneCode: String, phoneNumber: String)
         = !phoneCode.isEmpty() && !phoneNumber.isEmpty()

   private fun createPhoto(model: ProfileViewModel): SmartCardUserPhoto? =
         if (!model.isPhotoEmpty) SmartCardUserPhoto(model.chosenPhotoUri!!) else null

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
            lastName = profile.lastName,
            phoneNumber = createPhone(profile),
            userPhoto = createPhoto(profile))
   }

   fun provideInitialPhotoUrl(userPhotoUrl: String?): String? =
         if (userPhotoUrl != null && !WalletProfileUtils.isPhotoEmpty(userPhotoUrl)) userPhotoUrl else null
}
