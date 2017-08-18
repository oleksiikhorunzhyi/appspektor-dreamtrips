package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import java.util.Locale;

import rx.functions.Action0;
import rx.functions.Action1;

import static com.worldventures.dreamtrips.core.utils.ProjectTextUtils.isEmpty;

public class WalletProfileUtils {
   private static final String DEFAULT_AVATAR_URL = "/avatars/thumb/missing.png";

   public static boolean isPhotoEmpty(String chosenPhotoUri) {
      return chosenPhotoUri == null || chosenPhotoUri.equals(DEFAULT_AVATAR_URL) || chosenPhotoUri.isEmpty();
   }

   public static boolean equalsPhone(@Nullable SmartCardUserPhone phone1, @Nullable SmartCardUserPhone phone2) {
      return phone1 == phone2 || (phone1 != null && phone1.equals(phone2));
   }

   public static boolean equalsPhone(@Nullable SmartCardUserPhone phone, String countryCode, String number) {
      boolean isStrPhoneEmpty = isEmpty(countryCode) || isEmpty(number);
      if (!countryCode.contains("+")) {
         countryCode = String.format(Locale.getDefault(), "+%s", countryCode);
      }
      return (phone == null && isStrPhoneEmpty) ||
            (phone != null && phone.code().equals(countryCode) && phone.number().equals(number));
   }

   public static boolean equalsPhoto(@Nullable SmartCardUserPhoto photo, @Nullable String photoUri) {
      return (photo == null && photoUri == null) || (photo != null && photo.uri().equals(photoUri));
   }

   public static void checkUserNameValidation(String firstName, String middleName, String lastName,
         Action0 actionSuccess, Action1<Exception> actionError) {
      try {
         WalletValidateHelper.validateUserFullNameOrThrow(firstName, middleName, lastName);
      } catch (Exception e) {
         actionError.call(e);
         return;
      }
      actionSuccess.call();
   }
}
