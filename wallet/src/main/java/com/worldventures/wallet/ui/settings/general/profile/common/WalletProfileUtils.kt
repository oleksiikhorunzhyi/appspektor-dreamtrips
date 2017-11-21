package com.worldventures.wallet.ui.settings.general.profile.common

import com.worldventures.wallet.domain.entity.SmartCardUserPhone
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto
import com.worldventures.wallet.util.WalletValidateHelper
import rx.functions.Action0
import rx.functions.Action1

internal const val WALLET_PROFILE_DEFAULT_AVATAR_URL = "/avatars/thumb/missing.png"

object WalletProfileUtils {

   fun isPhotoEmpty(chosenPhotoUri: String?): Boolean =
         chosenPhotoUri.isNullOrEmpty() || chosenPhotoUri == WALLET_PROFILE_DEFAULT_AVATAR_URL

   fun equalsPhone(phone1: SmartCardUserPhone?, phone2: SmartCardUserPhone?) = phone1 == phone2

   fun equalsPhone(phone: SmartCardUserPhone?, countryCode: String, number: String): Boolean {
      var countryCodeLocal = countryCode
      val isStrPhoneEmpty = countryCodeLocal.isEmpty() || number.isEmpty()
      if (!countryCodeLocal.contains("+")) {
         countryCodeLocal = "+$countryCodeLocal"
      }
      return phone == null && isStrPhoneEmpty || phone != null && phone.code == countryCodeLocal && phone.number == number
   }

   @Deprecated("")
   fun equalsPhoto(photo: SmartCardUserPhoto?, photoUri: String?): Boolean =
         photo == null && photoUri == null || photo != null && photo.uri == photoUri

   @Deprecated("")
   fun checkUserNameValidation(firstName: String, middleName: String, lastName: String,
                               actionSuccess: Action0, actionError: Action1<Exception>) {
      try {
         WalletValidateHelper.validateUserFullNameOrThrow(firstName, middleName, lastName)
      } catch (e: Exception) {
         actionError.call(e)
         return
      }

      actionSuccess.call()
   }
}
