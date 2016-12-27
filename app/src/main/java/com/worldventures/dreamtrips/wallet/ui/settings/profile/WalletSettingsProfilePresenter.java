package com.worldventures.dreamtrips.wallet.ui.settings.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardManager;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UploadProfileDataException;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.JanetException;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfilePresenter.Screen, WalletSettingsProfileState> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardManager smartCardManager;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;

   private SmartCardUserPhoto preparedPhoto;
   private int changeProfileFlag = 0;

   public WalletSettingsProfilePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   // View State
   @Override
   public void onNewViewState() {
      state = new WalletSettingsProfileState();
   }

   @Override
   public void applyViewState() {
      super.applyViewState();
      changeProfileFlag = state.getChangeProfileFlag();
      preparedPhoto = state.getUserPhoto();
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state.setChangeProfileFlag(changeProfileFlag);
      state.setUserPhoto(preparedPhoto);
      super.onSaveInstanceState(bundle);
   }

   // bind to view
   @Override
   public void attachView(WalletSettingsProfilePresenter.Screen view) {
      super.attachView(view);
      setupInputMode();
      observePickerAndCropper(view);
      observeCompressingAvatar();
      observeUpdating();
      observeChanging();

      smartCardManager.singleSmartCardObservable()
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> {
               view.setPreviewPhoto(it.user().userPhoto().monochrome());
               view.setUserName(it.user().firstName(), it.user().middleName(), it.user().lastName());
            });
   }

   void setupUserData() {
      if (!isDataChanged()) {
         goBack();
         return;
      }
      final Screen view = getView();
      //noinspection all
      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .send(new UpdateSmartCardUserCommand(
                  ImmutableChangedFields.builder()
                        .firstName(view.getFirstName())
                        .middleName(view.getMiddleName())
                        .lastName(view.getLastName())
                        .photo(preparedPhoto)
                        .build()));
   }

   void cancelUploadServerUserData() {
      smartCardUserDataInteractor.revertSmartCardUserUpdatingPipe().send(new RevertSmartCardUserUpdatingCommand());
   }

   void setupInputMode() {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   void checkChangingAndGoBack() {
      getView().hidePhotoPicker();

      if (!isDataChanged()) {
         goBack();
      } else {
         getView().showRevertChangesDialog();
      }
   }

   void goBack() {
      navigator.goBack();
   }

   void retryUploadToServer() {
      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe().send(new RetryHttpUploadUpdatingCommand());
   }

   void cancelUpdating() {
   }

   void choosePhoto() {
      getView().pickPhoto();
   }

   private void observePickerAndCropper(WalletSettingsProfilePresenter.Screen view) {
      view.observePickPhoto().compose(bindView()).subscribe(view::cropPhoto);
      view.observeCropper().compose(bindView()).subscribe(this::onImageCropped);
   }

   private void observeUpdating() {
      final Screen view = getView();

      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.updateSmartCardUserPipe()))
            .subscribe(new ActionStateSubscriber<UpdateSmartCardUserCommand>()
                  .onStart(o -> view.showProgress())
                  .onSuccess(o -> {
                     view.hideProgress();
                     goBack();
                  })
                  .onFail((o, throwable) -> onError((JanetException) throwable))
            );

      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()))
            .subscribe(new ActionStateSubscriber<RetryHttpUploadUpdatingCommand>()
                  .onStart(o -> view.showProgress())
                  .onSuccess(o -> view.hideProgress())
                  .onFail((o, throwable) -> onError((JanetException) throwable))
            );
   }

   private void onError(JanetException exception) {
      final Screen view = getView();
      view.hideProgress();
      if (exception.getCause() instanceof NetworkUnavailableException) {
         view.showNetworkUnavailableError();
      } else if (exception.getCause() instanceof UploadProfileDataException) {
         view.showUploadServerError();
      } else {
         view.showError(exception.getCause());
      }
   }

   private void observeCompressingAvatar() {
      smartCardUserDataInteractor.smartCardAvatarPipe()
            .observeSuccess()
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> photoPrepared(command.getResult()));
   }

   private void photoPrepared(SmartCardUserPhoto photo) {
      preparedPhoto = photo;
      getView().setPreviewPhoto(photo.monochrome());
   }

   private void onImageCropped(String path) {
      smartCardUserDataInteractor.smartCardAvatarPipe().send(new CompressImageForSmartCardCommand(path));
   }

   private boolean isDataChanged() {
      return changeProfileFlag != 0;
   }

   private void observeChanging() {
      Screen view = getView();
      //noinspection all
      Observable.combineLatest(
            smartCardManager.smartCardObservable(),
            view.firstNameObservable(),
            view.middleNameObservable(),
            view.lastNameObservable(),
            smartCardUserDataInteractor.smartCardAvatarPipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .startWith(Observable.just(null)),
            (smartCard, firstName, middleName, lastName, newAvatar) -> {
               handleFirstName(smartCard.user().firstName(), firstName);
               handleMiddleName(smartCard.user().middleName(), middleName);
               handleLastName(smartCard.user().lastName(), lastName);
               handleAvatar(smartCard.user().userPhoto(), newAvatar);
               return null;
            })
            .compose(bindView())
            .subscribe();
   }

   private void handleFirstName(String firstName, String newFirstName) {
      if (newFirstName.equals(firstName)) {
         changeProfileFlag &= 0xFF_FF_FF_00;
      } else {
         changeProfileFlag |= 0x00_00_00_FF;
      }
   }

   private void handleMiddleName(String middleName, String newMiddleName) {
      if (middleName.equals(newMiddleName)) {
         changeProfileFlag &= 0xFF_FF_00_FF;
      } else {
         changeProfileFlag |= 0x00_00_FF_00;
      }
   }

   private void handleLastName(String lastName, String newLastName) {
      if (lastName.equals(newLastName)) {
         changeProfileFlag &= 0xFF_00_FF_FF;
      } else {
         changeProfileFlag |= 0x00_FF_00_00;
      }
   }

   private void handleAvatar(SmartCardUserPhoto userPhoto, SmartCardUserPhoto newUserPhoto) {
      if ((userPhoto == null && newUserPhoto != null) ||
            (userPhoto != null && newUserPhoto != null && !newUserPhoto.original().equals(userPhoto.original()))) {
         // new photo
         changeProfileFlag |= 0xFF_00_00_00;
      } else {
         changeProfileFlag &= 0x00_FF_FF_FF;
      }
   }

   public interface Screen extends WalletScreen {
      void pickPhoto();
      void hidePhotoPicker();
      void cropPhoto(String photoPath);

      Observable<String> observePickPhoto();
      Observable<String> observeCropper();

      void setPreviewPhoto(File file);
      void setUserName(String firstName, String middleName, String lastName);

      void showRevertChangesDialog();

      String getFirstName();
      String getMiddleName();
      String getLastName();

      Observable<String> firstNameObservable();
      Observable<String> middleNameObservable();
      Observable<String> lastNameObservable();

      void showError(Throwable throwable);
      void showUploadServerError();
      void showNetworkUnavailableError();

      void showProgress();
      void hideProgress();
   }
}
