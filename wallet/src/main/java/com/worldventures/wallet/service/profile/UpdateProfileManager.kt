package com.worldventures.wallet.service.profile

import com.worldventures.dreamtrips.api.smart_card.user_info.UpdateCardUserHttpAction
import com.worldventures.dreamtrips.api.smart_card.user_info.model.CardUserPhone
import com.worldventures.dreamtrips.api.smart_card.user_info.model.ImmutableUpdateCardUserData
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.SmartCardUserCommand
import io.techery.janet.Janet
import io.techery.mappery.MapperyContext
import rx.Observable
import rx.schedulers.Schedulers

class UpdateProfileManager(private val janetApi: Janet,
                           private val mapperyContext: MapperyContext,
                           private val interactor: SmartCardInteractor) {

   fun uploadData(smartCardId: String, newUser: SmartCardUser) =
         uploadToServerAndSave(smartCardId, newUser)

   private fun uploadToServerAndSave(smartCardId: String, newUser: SmartCardUser): Observable<SmartCardUser> {
      return janetApi.createPipe(UpdateCardUserHttpAction::class.java, Schedulers.io())
            .createObservableResult(UpdateCardUserHttpAction(java.lang.Long.parseLong(smartCardId), createServerModel(newUser)))
            .map<Any> { null }
            .onErrorReturn { throwable -> Observable.error<Any>(UploadProfileDataException(throwable)) }
            .flatMap { save(newUser) }
   }

   private fun save(newUser: SmartCardUser): Observable<SmartCardUser> {
      return interactor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.update({ newUser }))
            .map { it.result }
   }

   private fun createServerModel(newUser: SmartCardUser): UpdateCardUserData {
      return ImmutableUpdateCardUserData.builder()
            .photoUrl(newUser.userPhoto?.uri)
            .firstName(newUser.firstName)
            .middleName(newUser.middleName)
            .lastName(newUser.lastName)
            .phone(newUser.phoneNumber?.let { mapperyContext.convert(it, CardUserPhone::class.java) })
            .build()
   }
}
