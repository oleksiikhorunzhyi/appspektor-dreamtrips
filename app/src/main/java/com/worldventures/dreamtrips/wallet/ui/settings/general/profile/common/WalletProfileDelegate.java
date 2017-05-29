package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;

public class WalletProfileDelegate {

   private AnalyticsInteractor analyticsInteractor;

   public WalletProfileDelegate(
         AnalyticsInteractor analyticsInteractor) {
      this.analyticsInteractor = analyticsInteractor;
   }

   public void setupInputMode(Activity activity) {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   public void sendAnalytics(WalletAnalyticsAction action) {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(action);
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   public void observePickerAndCropper(WalletProfilePhotoView view) {
      view.observePickPhoto().compose(view.lifecycle()).subscribe(view::cropPhoto);
      view.observeCropper().compose(view.lifecycle()).subscribe(photoFile -> { /*nothing*/ });
   }

   @Nullable
   public SmartCardUserPhone createPhone(ProfileViewModel model) {
      if (ProjectTextUtils.isEmpty(model.getPhoneCode()) || ProjectTextUtils.isEmpty(model.getPhoneNumber())) {
         return null;
      } else {
         return SmartCardUserPhone.of(model.getPhoneCode(), model.getPhoneNumber());
      }
   }

   @Nullable
   public SmartCardUserPhoto createPhoto(ProfileViewModel model) {
      return !model.isPhotoEmpty() ? SmartCardUserPhoto.of(model.getChosenPhotoUri()) : null;
   }

   public ProfileViewModel toViewModel(SmartCardUser user) {
      final SmartCardUserPhone phone = user.phoneNumber();
      final SmartCardUserPhoto photo = user.userPhoto();
      final ProfileViewModel model = new ProfileViewModel();

      model.setFirstName(user.firstName());
      model.setMiddleName(user.middleName());
      model.setLastName(user.lastName());

      if (phone != null) {
         model.setPhoneCode(phone.code());
         model.setPhoneNumber(phone.number());
      }
      model.setChosenPhotoUri(photo != null ? photo.uri() : null);
      return model;
   }

   public ProfileViewModel toViewModel(User user) {
      final ProfileViewModel model = new ProfileViewModel();
      model.setFirstName(user.getFirstName());
      model.setLastName(user.getLastName());
      if (user.getAvatar() != null) {
         model.setChosenPhotoUri(user.getAvatar().getThumb());
      }
      return model;
   }
}
