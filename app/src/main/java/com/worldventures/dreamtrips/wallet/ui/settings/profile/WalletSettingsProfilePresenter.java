package com.worldventures.dreamtrips.wallet.ui.settings.profile;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardManager;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UploadProfileDataException;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfilePresenter.Screen, WalletSettingsProfileState> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardManager smartCardManager;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;

   private SmartCardUserPhoto preparedPhoto; // todo save to state
   private int changeNameFlag = 0;

   public WalletSettingsProfilePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(WalletSettingsProfilePresenter.Screen view) {
      super.attachView(view);
      setupInputMode();
      observePickerAndCropper(view);
      observeCompressingAvatar();
      observeUpdating();
      observeNameChanging();

      smartCardManager.singleSmartCardObservable()
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> {
               view.setPreviewPhoto(it.user().userPhoto().monochrome());
               view.setUserName(it.user().firstName(), it.user().middleName(), it.user().lastName());;
            });
   }

   private void observePickerAndCropper(WalletSettingsProfilePresenter.Screen view) {
      view.observePickPhoto().compose(bindView()).subscribe(view::cropPhoto);
      view.observeCropper().compose(bindView()).subscribe(this::onImageCropped);
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

   private void observeUpdating() {
      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.updateSmartCardUserPipe()))
            .filter(actionState -> actionState.status != ActionState.Status.PROGRESS)
            .subscribe(this::handleUpdateSmartCardUserCommand);
   }

   private void handleUpdateSmartCardUserCommand(ActionState<UpdateSmartCardUserCommand> actionState) {
      final Screen view = getView();

      switch (actionState.status) {
         case START:
            view.showProgress();
            break;
         case SUCCESS:
            view.hideProgress();
            break;
         case FAIL:
            view.hideProgress();
            if (actionState.exception.getCause() instanceof UploadProfileDataException) {
               view.showUploadServerFailDialog();
            } else {
               view.showError(actionState.exception.getCause());
            }
            break;
      }
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
      goBack();
   }

   void choosePhoto() {
      getView().pickPhoto();
   }

   private void observeCompressingAvatar() {
      smartCardUserDataInteractor.smartCardAvatarPipe()
            .observe() // todo observeSuccessWithReplay && clear pipe in wizard
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardAvatarCommand>()
                  .onSuccess(command -> photoPrepared(command.getResult())));
   }

   private void photoPrepared(SmartCardUserPhoto photo) {
      preparedPhoto = photo;
      getView().setPreviewPhoto(photo.monochrome());
   }

   private void onImageCropped(String path) {
      smartCardUserDataInteractor.smartCardAvatarPipe().send(new CompressImageForSmartCardCommand(path));
   }

   private boolean isDataChanged() {
      return changeNameFlag != 0 || preparedPhoto != null;
   }

   private void observeNameChanging() {
      Screen view = getView();
      //noinspection all
      Observable.combineLatest(
            smartCardManager.smartCardObservable(),
            view.firstNameObservable(),
            view.middleNameObservable(),
            view.lastNameObservable(),
            (smartCard, firstName, middleName, lastName) -> {
               handleFirstName(smartCard.user().firstName(), firstName);
               handleMiddleName(smartCard.user().middleName(), middleName);
               handleLastName(smartCard.user().lastName(), lastName);
               return null;
            })
            .compose(bindView())
            .subscribe();
   }

   private void handleFirstName(String firstName, String newFirstName) {
      if (newFirstName.equals(firstName)) {
         changeNameFlag &= 0xFF_FF_FF_00;
      } else {
         changeNameFlag |= 0x00_00_00_FF;
      }
   }

   private void handleMiddleName(String middleName, String newMiddleName) {
      if (middleName.equals(newMiddleName)) {
         changeNameFlag &= 0xFF_FF_00_FF;
      } else {
         changeNameFlag |= 0x00_00_FF_00;
      }
   }

   private void handleLastName(String lastName, String newLastName) {
      if (lastName.equals(newLastName)) {
         changeNameFlag &= 0xFF_00_FF_FF;
      } else {
         changeNameFlag |= 0x00_FF_00_00;
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
      void showUploadServerFailDialog();

      void showProgress();
      void hideProgress();
   }
}
