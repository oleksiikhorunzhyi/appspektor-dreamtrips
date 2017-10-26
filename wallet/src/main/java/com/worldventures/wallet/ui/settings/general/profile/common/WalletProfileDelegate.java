package com.worldventures.wallet.ui.settings.general.profile.common;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.settings.ProfileChangesSavedAction;
import com.worldventures.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand;

import java.util.Locale;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WalletProfileDelegate {

   private SmartCardUserDataInteractor smartCardUserDataInteractor;
   private WalletAnalyticsInteractor analyticsInteractor;

   public WalletProfileDelegate(SmartCardUserDataInteractor smartCardUserDataInteractor,
         WalletAnalyticsInteractor analyticsInteractor) {
      this.smartCardUserDataInteractor = smartCardUserDataInteractor;
      this.analyticsInteractor = analyticsInteractor;
   }

   public void observeProfileUploading(UpdateSmartCardUserView view) {
      observeProfileUploading(view, null, null);
   }

   public void observeProfileUploading(UpdateSmartCardUserView view,
         @Nullable Action1<SmartCardUser> onSuccess, @Nullable Action1<Throwable> onFailure) {

      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.updateSmartCardUserPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideUpdateSmartCardOperation(this))
                  .onSuccess(setupUserDataCommand -> {
                     sendAnalytics(new ProfileChangesSavedAction());
                     if (onSuccess != null) onSuccess.call(setupUserDataCommand.getResult());
                  })
                  .onFail((command, throwable) -> {
                     if (onFailure != null) onFailure.call(throwable);
                  })
                  .create());

      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()
            .observeWithReplay()
            .compose(view.bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideHttpUploadOperation(this)).create());
   }

   void cancelUploadServerUserData() {
      smartCardUserDataInteractor.revertSmartCardUserUpdatingPipe().send(new RevertSmartCardUserUpdatingCommand());
   }

   void retryUploadToServer() {
      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe().send(new RetryHttpUploadUpdatingCommand());
   }

   public void sendAnalytics(WalletAnalyticsAction action) {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(action);
      analyticsInteractor.walletAnalyticsPipe().send(analyticsCommand);
   }

   public void observePickerAndCropper(WalletProfilePhotoView view) {
      //      view.observeCropper().compose(view.lifecycle()).subscribe(photoFile -> { /*nothing*/ });
   }

   @Nullable
   public SmartCardUserPhone createPhone(ProfileViewModel model) {
      if (ProjectTextUtils.isEmpty(model.getPhoneCode()) || ProjectTextUtils.isEmpty(model.getPhoneNumber())) {
         return null;
      } else {
         return SmartCardUserPhone.of(
               String.format(Locale.getDefault(), "+%s", model.getPhoneCode()),
               model.getPhoneNumber());
      }
   }

   @Nullable
   public SmartCardUserPhoto createPhoto(ProfileViewModel model) {
      //noinspection all
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
         model.setPhoneCode(phone.code().replace("+", ""));
         model.setPhoneNumber(phone.number());
      }
      model.setChosenPhotoUri(photo != null ? photo.uri() : null);
      return model;
   }

   public ProfileViewModel toViewModel(String firstName, String lastName, String photoUri) {
      final ProfileViewModel model = new ProfileViewModel();
      model.setFirstName(firstName);
      model.setLastName(lastName);
      if (photoUri != null) {
         model.setChosenPhotoUri(photoUri);
      }
      return model;
   }

   public SmartCardUser createSmartCardUser(ProfileViewModel profile) {
      return ImmutableSmartCardUser.builder()
            .firstName(profile.getFirstName())
            .middleName(profile.getMiddleName())
            .lastName(profile.getLastNameWithSuffix())
            .phoneNumber(createPhone(profile))
            .userPhoto(createPhoto(profile))
            .build();
   }

   public String provideInitialPhotoUrl(String userPhotoUrl) {
      return (userPhotoUrl != null && !WalletProfileUtils.isPhotoEmpty(userPhotoUrl)) ? userPhotoUrl : null;
   }
}
