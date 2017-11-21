package com.worldventures.wallet.service.command.wizard

import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData
import com.worldventures.janet.injection.InjectableAction
import com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.domain.storage.WalletStorage
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.SmartCardUserCommand
import com.worldventures.wallet.service.command.http.AssociateCardUserCommand
import com.worldventures.wallet.service.command.uploadery.SmartCardUploaderyCommand
import com.worldventures.wallet.util.WalletFeatureHelper
import io.techery.janet.Command
import io.techery.janet.Janet
import io.techery.janet.command.annotations.CommandAction
import io.techery.mappery.MapperyContext
import rx.Observable
import rx.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

@CommandAction
class WizardCompleteCommand : Command<Void>(), InjectableAction {

   @field:[Inject Named(JANET_WALLET)]
   lateinit var walletJanet: Janet
   @Inject lateinit var interactor: SmartCardInteractor
   @Inject lateinit var walletStorage: WalletStorage
   @Inject lateinit var mapperyContext: MapperyContext
   @Inject lateinit var featureHelper: WalletFeatureHelper

   @Throws(Throwable::class)
   override fun run(callback: Command.CommandCallback<Void>) {
      val smartCard = walletStorage.smartCard

      uploadUserPhoto(smartCard!!.smartCardId)
            .flatMap { user ->
               walletJanet.createPipe(AssociateCardUserCommand::class.java)
                     .createObservableResult(AssociateCardUserCommand(smartCard.smartCardId, createRequestData(user)))
                     .flatMap { featureHelper.onUserAssigned(user) }
            }
            .subscribe({ callback.onSuccess(null) }, { callback.onFail(it) })
   }

   private fun uploadUserPhoto(smartCardId: String): Observable<SmartCardUser> {
      return interactor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .map { it.result }
            .flatMap { user -> checkUserPhotoAndUploadToServer(smartCardId, user) }
            .flatMap { user ->
               interactor.smartCardUserPipe().createObservableResult(SmartCardUserCommand.save(user))
                     .observeOn(Schedulers.trampoline())
                     .map { user }
            }
   }

   private fun checkUserPhotoAndUploadToServer(smartCardId: String, user: SmartCardUser): Observable<SmartCardUser> {
      val photo = user.userPhoto ?: return Observable.just(user)
      return uploadPhotoOnServer(smartCardId, photo)
            .map { photoUrl -> user.copy(userPhoto = SmartCardUserPhoto(photoUrl)) }
   }

   private fun uploadPhotoOnServer(smartCardId: String, photo: SmartCardUserPhoto): Observable<String> {
      return walletJanet.createPipe(SmartCardUploaderyCommand::class.java, Schedulers.io())
            .createObservableResult(SmartCardUploaderyCommand(smartCardId, photo.uri))
            .map { c -> c.result.response().uploaderyPhoto().location() }
   }

   private fun createRequestData(smartCardUser: SmartCardUser): UpdateCardUserData {
      val photo = smartCardUser.userPhoto
      val smartCardUserPhone = smartCardUser.phoneNumber
      val userBuilder = ImmutableUpdateCardUserData.builder()
            .firstName(smartCardUser.firstName)
            .lastName(smartCardUser.lastName)
            .middleName(smartCardUser.middleName)
            .photoUrl(photo?.uri ?: "")
      if (smartCardUserPhone != null) {
         userBuilder.phone(mapperyContext.convert(smartCardUserPhone, CardUserPhone::class.java))
      }
      return userBuilder.build()
   }
}
