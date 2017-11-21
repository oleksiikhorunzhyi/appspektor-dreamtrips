package com.worldventures.wallet.service.profile

import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.WalletSocialInfoProvider
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhone
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.janet.smartcard.action.user.UpdateUserAction
import io.techery.janet.smartcard.model.ImmutableUser
import io.techery.janet.smartcard.model.User
import rx.Observable
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class RevertSmartCardUserUpdatingCommand(smartCardId: String, newUser: SmartCardUser)
   : BaseUserUpdateCommand<Void>(smartCardId, newUser), InjectableAction {

   @field:[Inject Named(JANET_WALLET)]
   lateinit var janet: Janet
   @Inject lateinit var socialInfoProvider: WalletSocialInfoProvider

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      janet.createPipe(SmartCardUserCommand::class.java)
            .createObservableResult(SmartCardUserCommand.fetch())
            .flatMap { revertUpdating(smartCardId, it.result) }
            .subscribe({ callback.onSuccess(it) }, { callback.onFail(it) })
   }

   private fun revertUpdating(smartCardId: String, user: SmartCardUser): Observable<Void> {
      return revertName(newUser, user, smartCardId)
            .flatMap { revertPhoto(newUser, user) }
   }

   private fun revertName(newUser: SmartCardUser, user: SmartCardUser, smartCardId: String): Observable<Void> {
      val userChanged = (newUser.firstName == user.firstName
            && newUser.middleName == user.middleName
            && newUser.lastName == user.lastName
            && equalsPhone(newUser.phoneNumber, user.phoneNumber))
      return if (userChanged) {
         janet.createPipe(UpdateUserAction::class.java)
               .createObservableResult(UpdateUserAction(createUser(user, smartCardId)))
               .map { null }
      } else {
         Observable.just(null)
      }
   }

   private fun revertPhoto(newUser: SmartCardUser, user: SmartCardUser): Observable<Void> {
      val userPhoto = user.userPhoto
      return if (newUser.userPhoto == null || userPhoto == null) {
         Observable.just(null)
      } else janet.createPipe(UpdateSmartCardUserPhotoCommand::class.java)
            .createObservableResult(UpdateSmartCardUserPhotoCommand(userPhoto.uri))
            .map { null }
   }

   private fun createUser(user: SmartCardUser, smartCardId: String): User {
      return ImmutableUser.builder()
            .firstName(user.firstName)
            .middleName(user.middleName)
            .lastName(user.lastName)
            .isUserAssigned(true)
            .memberId(socialInfoProvider.userId()!!.toLong())
            .barcodeId(java.lang.Long.parseLong(smartCardId))
            .memberStatus(socialInfoProvider.memberStatus()!!)
            .build()
   }
}
