package com.worldventures.dreamtrips.wallet.ui.settings.profile;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardManager;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.UpdateCardUserServerDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfilePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject Activity activity;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;
   @Inject SmartCardManager smartCardManager;

   @Nullable private SmartCardUserPhoto preparedPhoto;

   private SmartCard baseValue;

   public WalletSettingsProfilePresenter(Context context, Injector injector) {
      super(context, injector);
   }


   @Override
   public void attachView(WalletSettingsProfilePresenter.Screen view) {
      super.attachView(view);
      setupInputMode();
      observePickerAndCropper(view);
      subscribePreparingAvatarCommand();

      smartCardManager.singleSmartCardObservable()
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> {
               this.baseValue = it;
               preparedPhoto = baseValue.user().userPhoto();
               view.setPreviewPhoto(Uri.fromFile(it.user().userPhoto().monochrome()));
               view.setUserName(it.user().firstName(), it.user().middleName(), it.user().lastName());
            }, throwable -> {
               Timber.e(throwable, "");
            });
   }

   private void observePickerAndCropper(WalletSettingsProfilePresenter.Screen view) {
      view.observePickPhoto().compose(bindView()).subscribe(view::cropPhoto);
      view.observeCropper().compose(bindView()).subscribe(this::onImageCropped);
   }

   void setupUserData() {
      if (!isDataChanged()) {
         goBack(true);
         return;
      }

      getView().showProgress();
      setupCardUserData(getView().getFirstName(), getView().getMiddleName(), getView().getLastName(), preparedPhoto)
            .subscribe(smartCardCommand -> uploadDataToServer(), throwable -> {
               String text = getContext().getString(throwable instanceof FormatException?
                     R.string.wallet_add_card_details_error_message : R.string.wallet_card_settings_profile_dialog_error_smartcard_content);
               getView().showUploadSmartCardFailDialog(text);
            });
   }

   void cancelUploadSmartUserData(){
      getView().hideProgress();
   }

   void uploadDataToServer() {
      smartCardUserDataInteractor.updateCardUserServerDataCommandPipe()
            .createObservableResult(new UpdateCardUserServerDataCommand(getView().getFirstName(), getView().getMiddleName(), getView()
                  .getLastName(), preparedPhoto, baseValue.smartCardId()))
            .compose(bindViewIoToMainComposer())
            .subscribe(obj -> {
               getView().hideProgress();
               goBack(true);
            }, throwable -> getView().showUploadServerFailDialog());
   }

   void cancelUploadServerUserData() {
      setupCardUserData(baseValue.user().firstName(), baseValue.user().middleName(), baseValue.user().lastName(), baseValue.user().userPhoto())
            .compose(bindViewIoToMainComposer())
            .subscribe(smartCardCommand -> getView().hideProgress(), throwable ->
                  Timber.e(throwable, "Error while restore old information"));
   }

   private Observable<SmartCard> setupCardUserData(String firstName, String middleName, String lastName, SmartCardUserPhoto userPhoto) {
      SetupUserDataCommand command = new SetupUserDataCommand(firstName, middleName, lastName, userPhoto, baseValue.smartCardId());

      return smartCardUserDataInteractor.setupUserDataCommandPipe()
            .createObservableResult(command)
            .compose(bindViewIoToMainComposer())
            .map(Command::getResult);
   }

   private void subscribePreparingAvatarCommand() {
      smartCardUserDataInteractor.smartCardAvatarPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardAvatarCommand>()
                  .onFail((command, throwable) -> Timber.e("", throwable))
                  .onSuccess(command -> photoPrepared(command.getResult())));
   }

   private void photoPrepared(SmartCardUserPhoto photo) {
      preparedPhoto = photo;
      getView().setPreviewPhoto(Uri.fromFile(photo.monochrome()));
   }


   private void onImageCropped(String path) {
      smartCardUserDataInteractor.smartCardAvatarPipe().send(new CompressImageForSmartCardCommand(path));
   }

   void setupInputMode() {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   public void goBack(boolean force) {
      if (!isDataChanged() || force) {
         getView().hidePhotoPicker();
         navigator.goBack();
      } else {
         getView().showRevertChangesDialog();
      }
   }

   private boolean isDataChanged() {
      String viewFirstName = getView().getFirstName();
      String viewMiddleName = getView().getMiddleName();
      String viewLastName = getView().getLastName();
      SmartCardUser user = baseValue.user();
      boolean isFirstNameEdited = !viewFirstName.equals(user.firstName());
      boolean isMiddleNameEdited = !viewMiddleName.equals(user.middleName());
      boolean isLastNameEdited = !viewLastName.equals(user.lastName());
      boolean isPhotoChanged = preparedPhoto != null && !preparedPhoto.original().equals(user.userPhoto().original());
      return isFirstNameEdited || isMiddleNameEdited || isLastNameEdited || isPhotoChanged;
   }

   void choosePhoto() {
      getView().pickPhoto();
   }

   public interface Screen extends WalletScreen {
      void pickPhoto();
      void cropPhoto(String photoPath);
      Observable<String> observePickPhoto();
      Observable<String> observeCropper();
      void hidePhotoPicker();
      void setPreviewPhoto(Uri uri);

      void setUserName(String firstName, String middleName, String lastName);

      void showRevertChangesDialog();

      String getFirstName();
      String getMiddleName();
      String getLastName();

      void showProgress();
      void hideProgress();
      void showUploadSmartCardFailDialog(String text);
      void showUploadServerFailDialog();
   }

}
