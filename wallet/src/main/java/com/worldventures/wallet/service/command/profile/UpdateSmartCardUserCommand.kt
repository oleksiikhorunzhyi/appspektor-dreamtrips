package com.worldventures.wallet.service.command.profile

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletNetworkService
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.settings.general.display.ValidateDisplayTypeDataCommand
import com.worldventures.wallet.service.command.uploadery.SmartCardUploaderyCommand
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhone
import com.worldventures.wallet.util.CachedPhotoUtil
import com.worldventures.wallet.util.FormatException
import com.worldventures.wallet.util.NetworkUnavailableException
import com.worldventures.wallet.util.WalletValidateHelper
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.action.user.RemoveUserPhotoAction
import io.techery.janet.smartcard.action.user.UpdateUserAction
import io.techery.janet.smartcard.model.ImmutableUser
import io.techery.mappery.MapperyContext
import rx.Observable
import rx.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class UpdateSmartCardUserCommand(newUser: SmartCardUser, smartCardId: String,
                                 private val forceUpdateDisplayType: Boolean)
   : BaseUserUpdateCommand<SmartCardUser>(smartCardId, newUser), InjectableAction {

   @field:[Inject Named(JANET_WALLET)]
   lateinit var janet: Janet
   @Inject lateinit var smartCardInteractor: SmartCardInteractor
   @Inject lateinit var networkService: WalletNetworkService
   @Inject lateinit var socialInfoProvider: WalletSocialInfoProvider
   @Inject lateinit var updateProfileManager: UpdateProfileManager
   @Inject lateinit var mapperyContext: MapperyContext
   @Inject lateinit var cachedPhotoUtil: CachedPhotoUtil

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<SmartCardUser>) {
      validateData()
      validateNetwork()

      smartCardInteractor.validateDisplayTypeDataPipe()
            .createObservableResult(ValidateDisplayTypeDataCommand(
                  newUser.userPhoto != null, newUser.phoneNumber != null, forceUpdateDisplayType))
            .flatMap {
               smartCardInteractor.smartCardUserPipe()
                     .createObservableResult(SmartCardUserCommand.fetch())
                     .flatMap { uploadData(smartCardId, it.result) }
            }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   @Throws(FormatException::class)
   private fun validateData() {
      WalletValidateHelper.validateUserFullNameOrThrow(newUser.firstName, newUser.middleName, newUser.lastName)
   }

   private fun validateNetwork() {
      if (!networkService.isAvailable) {
         throw NetworkUnavailableException()
      }
   }

   private fun uploadData(smartCardId: String, user: SmartCardUser): Observable<SmartCardUser> {
      return updateNameOnSmartCard(smartCardId, user)
            .flatMap {
               uploadPhotoIfNeed(user, smartCardId)
                     .map { newUser.copy(userPhoto = it?.let { SmartCardUserPhoto(it) }) }
            }
            .flatMap { updateCardUserData -> updateProfileManager.uploadData(smartCardId, updateCardUserData) }
   }

   private fun updateNameOnSmartCard(scId: String, сachedUser: SmartCardUser): Observable<Void> {
      return if (needToUpdate(newUser, сachedUser)) {
         janet.createPipe(UpdateUserAction::class.java)
               .createObservableResult(UpdateUserAction(ImmutableUser.builder() //todo duplicated code
                     .firstName(newUser.firstName)
                     .middleName(newUser.middleName)
                     .lastName(newUser.lastName)
                     .phoneNum(newUser.phoneNumber?.fullPhoneNumber())
                     .isUserAssigned(true)
                     .memberId(socialInfoProvider.userId()!!.toLong())
                     .barcodeId(java.lang.Long.parseLong(scId))
                     .memberStatus(socialInfoProvider.memberStatus()!!)
                     .build()))
               .map { null }
      } else {
         Observable.just(null)
      }
   }

   private fun needToUpdate(updatedUser: SmartCardUser, cachedUser: SmartCardUser): Boolean {
      return (updatedUser.firstName != cachedUser.firstName
            || updatedUser.middleName != cachedUser.middleName
            || updatedUser.lastName != cachedUser.lastName
            || !equalsPhone(updatedUser.phoneNumber, cachedUser.phoneNumber))
   }

   private fun uploadPhotoIfNeed(cachedUser: SmartCardUser, smartCardId: String): Observable<String?> {
      val newPhoto = newUser.userPhoto
      clearUserImageCache(cachedUser.userPhoto)

      return if (newPhoto != null) {
         janet.createPipe(UpdateSmartCardUserPhotoCommand::class.java)
               .createObservableResult(UpdateSmartCardUserPhotoCommand(newPhoto.uri))
               .flatMap {
                  janet.createPipe(SmartCardUploaderyCommand::class.java, Schedulers.io())
                        .createObservableResult(SmartCardUploaderyCommand(smartCardId, newPhoto.uri))
               }
               .map { it.result.response().uploaderyPhoto().location() }
      } else {
         smartCardInteractor.removeUserPhotoActionPipe()
               .createObservableResult(RemoveUserPhotoAction())
               .map { null }
      }
   }

   private fun clearUserImageCache(photo: SmartCardUserPhoto?) {
      photo?.let { cachedPhotoUtil.removeCachedPhoto(it.uri) }
   }
}
