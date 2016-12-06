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
import com.worldventures.dreamtrips.wallet.service.SmartCardAvatarInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardManager;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateUserDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfilePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject Activity activity;
   @Inject SmartCardAvatarInteractor smartCardAvatarInteractor;
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
               view.setPreviewPhoto(Uri.fromFile(it.user().userPhoto().original()));
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
      if (!isDataChanged()) {goBack(true);}

      smartCardInteractor.updateUserDataActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<UpdateUserDataCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(this::onDataSaved)
                  .onFail(ErrorHandler.<UpdateUserDataCommand>builder(getContext())
                        .handle(FormatException.class, R.string.wallet_add_card_details_error_message)
                        .build())
                  .wrap());

      smartCardInteractor.updateUserDataActionPipe()
            .send(new UpdateUserDataCommand(baseValue.user(), getView().getFirstName(), getView().getMiddleName(), getView()
                  .getLastName(), preparedPhoto, baseValue.smartCardId()));
   }

   private void onDataSaved(UpdateUserDataCommand setupUserDataCommand) {
      goBack(true);
   }

   private void subscribePreparingAvatarCommand() {
      smartCardAvatarInteractor.smartCardAvatarPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardAvatarCommand>()
                  .onFail((command, throwable) -> Timber.e("", throwable))
                  .onSuccess(command -> photoPrepared(command.getResult())));
   }

   private void photoPrepared(SmartCardUserPhoto photo) {
      preparedPhoto = photo;
      getView().setPreviewPhoto(Uri.fromFile(photo.original()));
   }


   private void onImageCropped(String path) {
      smartCardAvatarInteractor.smartCardAvatarPipe().send(new CompressImageForSmartCardCommand(path));
   }

   public void setupInputMode() {
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
   }

}
