package com.worldventures.wallet.service.command.profile

import com.worldventures.dreamtrips.api.smart_card.user_info.UpdateCardUserHttpAction
import com.worldventures.dreamtrips.api.smart_card.user_info.model.UpdateCardUserData
import com.worldventures.wallet.domain.entity.SmartCardUser
import com.worldventures.wallet.service.SmartCardInteractor
import com.worldventures.wallet.service.command.SmartCardUserCommand
import io.techery.janet.Janet
import rx.Observable
import rx.schedulers.Schedulers

class UpdateProfileManager(private val janetApi: Janet,
                                    private val interactor: SmartCardInteractor,
                                    private val updateDataHolder: UpdateDataHolder) {

   private var smartCardId: String? = null
   private var updateCardUserData: UpdateCardUserData? = null

   fun attachChangedFields(changedFields: ChangedFields) {
      updateDataHolder.saveChanging(changedFields)
   }

   fun uploadData(smartCardId: String, updateCardUserData: UpdateCardUserData): Observable<SmartCardUser> {
      this.smartCardId = smartCardId
      this.updateCardUserData = updateCardUserData
      return uploadToServerAndSave()
   }

   fun retryUploadData(): Observable<SmartCardUser> {
      //todo check input data and remove after data successfully uploaded
      return uploadToServerAndSave()
   }

   private fun uploadToServerAndSave(): Observable<SmartCardUser> {
      return janetApi.createPipe(UpdateCardUserHttpAction::class.java, Schedulers.io())
            .createObservableResult(UpdateCardUserHttpAction(java.lang.Long.parseLong(smartCardId), updateCardUserData))
            .map<Any> { null }
            .onErrorReturn { throwable -> Observable.error<Any>(UploadProfileDataException(throwable)) }
            .flatMap { save() }
   }

   private fun save(): Observable<SmartCardUser> {
      return interactor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.update({ this.bindNewFields(it) }))
            .map { it.result }
   }

   private fun bindNewFields(user: SmartCardUser): SmartCardUser {
      val changedFields = updateDataHolder.changedFields
      return user.copy(
            firstName = changedFields.firstName,
            middleName = changedFields.middleName,
            lastName = changedFields.lastName,
            phoneNumber = changedFields.phone,
            userPhoto = changedFields.photo)
   }
}
