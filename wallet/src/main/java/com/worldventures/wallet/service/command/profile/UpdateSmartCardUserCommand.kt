package com.worldventures.wallet.service.command.profile

import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.WalletNetworkService
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.command.ActiveSmartCardCommand
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.settings.general.display.ValidateDisplayTypeDataCommand
import com.worldventures.wallet.service.command.uploadery.SmartCardUploaderyCommand
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
class UpdateSmartCardUserCommand(private val changedFields: ChangedFields,
                                 private val forceUpdateDisplayType: Boolean) : Command<SmartCardUser>(), InjectableAction {

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
                  changedFields.photo != null, changedFields.phone != null, forceUpdateDisplayType))
            .doOnNext { updateProfileManager.attachChangedFields(changedFields) }
            .flatMap {
               Observable.zip<ActiveSmartCardCommand, SmartCardUserCommand, Pair<ActiveSmartCardCommand, SmartCardUserCommand>>(
                     smartCardInteractor.activeSmartCardPipe()
                           .createObservableResult(ActiveSmartCardCommand()),
                     smartCardInteractor.smartCardUserPipe()
                           .createObservableResult(SmartCardUserCommand.fetch()),
                     { first, second -> Pair(first, second) })
            }
            .flatMap { pair -> uploadData(pair.first.result.smartCardId, pair.second.result) }
            .subscribe( { callback.onSuccess(it) },  { callback.onFail(it) })
   }

   @Throws(FormatException::class)
   private fun validateData() {
      WalletValidateHelper.validateUserFullNameOrThrow(
            changedFields.firstName,
            changedFields.middleName,
            changedFields.lastName)
   }

   private fun validateNetwork() {
      if (!networkService.isAvailable) {
         throw NetworkUnavailableException()
      }
   }

   private fun uploadData(smartCardId: String, user: SmartCardUser): Observable<SmartCardUser> {
      return pushToSmartCard(smartCardId, user)
            .flatMap { updateCardUserData -> updateProfileManager.uploadData(smartCardId, updateCardUserData) }
   }

   private fun pushToSmartCard(smartCardId: String, user: SmartCardUser): Observable<UpdateCardUserData> {
      return updateNameOnSmartCard(smartCardId, user)
            .flatMap { userData -> uploadPhotoIfNeed(user, smartCardId, userData) }
   }

   private fun updateNameOnSmartCard(scId: String, user: SmartCardUser): Observable<UpdateCardUserData> {
      val dataBuilder = ImmutableUpdateCardUserData.builder()

      dataBuilder.photoUrl(if (changedFields.photo != null) changedFields.photo.uri else null)
      dataBuilder.firstName(changedFields.firstName)
      dataBuilder.middleName(changedFields.middleName)
      dataBuilder.lastName(changedFields.lastName)

      if (changedFields.phone != null) {
         dataBuilder.phone(mapperyContext.convert(changedFields.phone, CardUserPhone::class.java))
      }

      val userData = dataBuilder.build()
      return if (needToUpdate(user)) {
         janet.createPipe(UpdateUserAction::class.java)
               .createObservableResult(UpdateUserAction(ImmutableUser.builder()
                     .firstName(changedFields.firstName)
                     .middleName(changedFields.middleName)
                     .lastName(changedFields.lastName)
                     .phoneNum(changedFields.phone?.fullPhoneNumber())
                     .isUserAssigned(true)
                     .memberId(socialInfoProvider.userId()!!.toLong())
                     .barcodeId(java.lang.Long.parseLong(scId))
                     .memberStatus(socialInfoProvider.memberStatus()!!)
                     .build()))
               .map { userData }
      } else {
         Observable.just(userData)
      }
   }

   private fun needToUpdate(user: SmartCardUser): Boolean {
      return (changedFields.firstName != user.firstName
            || changedFields.middleName != user.middleName
            || changedFields.lastName != user.lastName
            || !equalsDirectly(user.phoneNumber, changedFields.phone))
   }

   private fun equalsDirectly(a: Any?, b: Any?): Boolean {
      return a === b || a != null && a == b
   }

   private fun uploadPhotoIfNeed(user: SmartCardUser, smartCardId: String,
                                 updateUserData: UpdateCardUserData): Observable<UpdateCardUserData> {
      val newPhoto = changedFields.photo
      if (newPhoto != null) {

         clearUserImageCache(user.userPhoto)

         return janet.createPipe(UpdateSmartCardUserPhotoCommand::class.java)
               .createObservableResult(UpdateSmartCardUserPhotoCommand(newPhoto.uri))
               .flatMap {
                  janet.createPipe(SmartCardUploaderyCommand::class.java, Schedulers.io())
                        .createObservableResult(SmartCardUploaderyCommand(smartCardId, newPhoto.uri))
               }
               .map { command ->
                  ImmutableUpdateCardUserData.builder()
                        .from(updateUserData)
                        //uri saved in UpdateProfileManager
                        .photoUrl(command.result.response().uploaderyPhoto().location())
                        .build()
               }
      } else {
         return smartCardInteractor.removeUserPhotoActionPipe()
               .createObservableResult(RemoveUserPhotoAction())
               .map { updateUserData }
      }
   }

   private fun clearUserImageCache(photo: SmartCardUserPhoto?) {
      if (photo != null) {
         cachedPhotoUtil.removeCachedPhoto(photo.uri)
      }
   }
}
