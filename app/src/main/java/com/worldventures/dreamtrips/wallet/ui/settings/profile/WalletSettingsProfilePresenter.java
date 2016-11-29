package com.worldventures.dreamtrips.wallet.ui.settings.profile;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.domain.storage.TemporaryStorage;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardAvatarInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.user.GetUserDataAction;
import rx.Observable;
import timber.log.Timber;

public class WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfilePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject FirmwareInteractor firmwareInteractor;
   @Inject TemporaryStorage temporaryStorage;
   @Inject Activity activity;
   @Inject SmartCardAvatarInteractor smartCardAvatarInteractor;
   @Inject WizardInteractor wizardInteractor;

   @Nullable private SmartCardUserPhoto preparedPhoto;

   public WalletSettingsProfilePresenter(Context context, Injector injector) {
      super(context, injector);
   }


   @Override
   public void attachView(WalletSettingsProfilePresenter.Screen view) {
      super.attachView(view);
      setupInputMode();
      observePickerAndCropper(view);
      subscribePreparingAvatarCommand();

      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> {
               view.setPreviewPhoto(Uri.fromFile(it.getResult().user().userPhoto().original()));
            }, throwable -> {
               Timber.e(throwable, "");
            });

      smartCardInteractor.userDataActionActionPipe()
            .createObservableResult(new GetUserDataAction())
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> {
               getView().setUserName(it.user.firstName(), it.user.middleName(), it.user.lastName());
            }, throwable -> {
               //todo
            });
   }

   private void observePickerAndCropper(WalletSettingsProfilePresenter.Screen view) {
      view.observePickPhoto().compose(bindView()).subscribe(view::cropPhoto);
      view.observeCropper().compose(bindView()).subscribe(this::onImageCropped);
   }

   void setupUserData() {

      //todo add validation
      smartCardInteractor.activeSmartCardPipe()
            .createObservableResult(new GetActiveSmartCardCommand())
            .doOnNext(it -> {
               if (it.getResult().connectionStatus() != SmartCard.ConnectionStatus.CONNECTED) {
                  throw new IllegalStateException("Smart card should be connected");
               }
            })
            .flatMap(it -> wizardInteractor.setupUserDataPipe()
                  .createObservableResult(new SetupUserDataCommand(getView().getFirstName(), getView().getMiddleName(), getView()
                        .getLastName(), preparedPhoto, it.getResult().smartCardId())))
            .compose(bindViewIoToMainComposer())
            .subscribe(setupUserDataCommand -> {
               Toast.makeText(getContext(), "SUCCESS", Toast.LENGTH_LONG).show();
               //todo
            }, throwable -> {
               Timber.e(throwable, "");
            });

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

      return true;
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
