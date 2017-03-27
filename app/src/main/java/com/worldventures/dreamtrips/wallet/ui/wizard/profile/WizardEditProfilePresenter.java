package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.SetupUserAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAndStoreDefaultAddressInfoCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup.WizardPinSetupPath;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.MissedAvatarException;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;
import timber.log.Timber;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SessionHolder<UserSession> appSessionHolder;

   @Nullable private SmartCardUserPhoto preparedPhoto;

   public WizardEditProfilePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new SetupUserAction()));
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      // TODO: 9/1/16 investigate and fix this problem (photo piker brake SoftInputMode too)
      // hotfix, web view brake SoftInputMode (set ADJUST_RESIZE)
      setupInputMode();

      observePickerAndCropper(view);
      subscribePreparingAvatarCommand();
      subscribeSetupUserCommand();
      fetchAndStoreDefaultAddress();

      User userProfile = appSessionHolder.get().get().getUser();
      view.setUserFullName(userProfile.getFirstName(), userProfile.getLastName());
      String defaultUserAvatar = userProfile.getAvatar().getThumb();
      if (!TextUtils.isEmpty(defaultUserAvatar)) {
         smartCardUserDataInteractor.smartCardAvatarPipe().send(SmartCardAvatarCommand.fromUrl(defaultUserAvatar));
      }
   }

   void setupInputMode() {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   private void observePickerAndCropper(Screen view) {
      view.observePickPhoto().compose(bindView()).subscribe(view::cropPhoto);

      view.observeCropper().compose(bindView()).subscribe(this::prepareImage);
   }

   private void subscribePreparingAvatarCommand() {
      smartCardUserDataInteractor.smartCardAvatarPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardAvatarCommand>()
                  .onFail((command, throwable) -> Timber.e("", throwable))
                  .onSuccess(command -> photoPrepared(command.getResult())));
   }

   private void subscribeSetupUserCommand() {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationView())
                  .onSuccess(command -> onUserSetupSuccess())
                  .create());
   }

   private void onUserSetupSuccess() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new PhotoWasSetAction()));
      navigator.go(new WizardPinSetupPath(Action.SETUP));
   }

   private void photoPrepared(SmartCardUserPhoto photo) {
      preparedPhoto = photo;
      getView().setPreviewPhoto(photo.photoUrl());
   }

   void goToBack() {
      getView().hidePhotoPicker();
      navigator.goBack();
   }

   void choosePhoto() {
      getView().pickPhoto();
   }

   private void prepareImage(String path) {
      smartCardUserDataInteractor.smartCardAvatarPipe().send(SmartCardAvatarCommand.fromSchemePath(path));
   }

   void setupUserData() {
      final String[] userNames = getView().getUserName();
      if (isUserDataValid(userNames))
         getView().showConfirmationDialog(String.format("%s %s", userNames[0], userNames[2]));
   }

   void onUserDataConfirmed() {
      final String[] userNames = getView().getUserName();
      wizardInteractor.setupUserDataPipe()
            .send(new SetupUserDataCommand(userNames[0], userNames[1], userNames[2], preparedPhoto));
   }

   private boolean isUserDataValid(String[] userNames) {
      try {
         if (preparedPhoto == null || preparedPhoto.original() == null || !preparedPhoto.original().exists()) {
            throw new MissedAvatarException();
         }
         WalletValidateHelper.validateUserFullNameOrThrow(userNames[0], userNames[1], userNames[2]);
      } catch (FormatException | MissedAvatarException e) {
         getView().provideOperationView().showError(null, e);
         return false;
      }
      return true;
   }

   private void fetchAndStoreDefaultAddress() {
      wizardInteractor
            .fetchAndStoreDefaultAddressInfoPipe().send(new FetchAndStoreDefaultAddressInfoCommand());
   }

   public interface Screen extends WalletScreen {

      OperationView<SetupUserDataCommand> provideOperationView();

      void pickPhoto();

      void cropPhoto(String photoPath);

      Observable<String> observePickPhoto();

      Observable<String> observeCropper();

      void hidePhotoPicker();

      void setPreviewPhoto(String photoUrl);

      void setUserFullName(String firstName, String lastName);

      @NonNull
      String[] getUserName();

      void showConfirmationDialog(String fullName);
   }
}
