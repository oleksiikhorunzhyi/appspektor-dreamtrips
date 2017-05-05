package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.settings.ProfileChangesSavedAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardProfileAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UploadProfileDataException;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.util.FirstNameException;
import com.worldventures.dreamtrips.wallet.util.LastNameException;
import com.worldventures.dreamtrips.wallet.util.MiddleNameException;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.JanetException;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.functions.Action0;
import timber.log.Timber;

public class WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfilePresenter.Screen, WalletSettingsProfileState> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private SmartCardUserPhoto preparedPhoto;
   private boolean profileDataIsChanged = false;

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
      profileDataIsChanged = state.getChangeProfileFlag();
      preparedPhoto = state.getUserPhoto();
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      state.setChangeProfileFlag(profileDataIsChanged);
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

      smartCardInteractor.smartCardUserPipe().createObservableResult(SmartCardUserCommand.fetch())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(it -> {
               view.setPreviewPhoto(it.userPhoto().photoUrl());
               view.setUserName(it.firstName(), it.middleName(), it.lastName());
               if (it.phoneNumber() != null) view.setPhone(it.phoneNumber().code(), it.phoneNumber().number());
            }, throwable -> Timber.e(throwable, ""));

      trackScreen();
   }

   private void trackScreen() {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new SmartCardProfileAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   void handleDoneAction() {
      //noinspection all
      final ChangedFields changedFields = collectUserData(getView());
      handleToolbarAction(() -> {
         smartCardUserDataInteractor.updateSmartCardUserPipe().send(new UpdateSmartCardUserCommand(changedFields));
         trackProfileChangesSaved();
      });
   }

   private void trackProfileChangesSaved() {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(new ProfileChangesSavedAction());
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   private ChangedFields collectUserData(Screen view) {
      return ImmutableChangedFields.builder()
            .firstName(view.getFirstName())
            .middleName(view.getMiddleName())
            .lastName(view.getLastName())
            .photo(preparedPhoto)
            .phone(ImmutableSmartCardUserPhone.of(view.getPhoneNumber(), view.getCountryCode()))
            .build();
   }

   void handleBackAction() {
      handleToolbarAction(() -> getView().showRevertChangesDialog());
   }

   private void handleToolbarAction(Action0 action) {
      getView().hidePhotoPicker();
      if (!isDataChanged()) {
         goBack();
         return;
      }
      action.call();
   }

   void cancelUploadServerUserData() {
      smartCardUserDataInteractor.revertSmartCardUserUpdatingPipe().send(new RevertSmartCardUserUpdatingCommand());
   }

   void setupInputMode() {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
            .subscribe(OperationActionStateSubscriberWrapper.<UpdateSmartCardUserCommand>forView(getView().provideOperationDelegate())
                  .onStart(getContext().getString(R.string.loading))
                  .onSuccess(setupUserDataCommand -> goBack())
                  .onFail(ErrorHandler.<UpdateSmartCardUserCommand>builder(getContext())
                        .handle(FirstNameException.class, R.string.wallet_edit_profile_first_name_format_detail)
                        .handle(LastNameException.class, R.string.wallet_edit_profile_last_name_format_detail)
                        .handle(MiddleNameException.class, R.string.wallet_edit_profile_middle_name_format_detail)
                        .handle(NetworkUnavailableException.class, updateSmartCardUserCommand -> view.showNetworkUnavailableError())
                        .handle(UploadProfileDataException.class, updateSmartCardUserCommand -> view.showUploadServerError())
                        .build())
                  .wrap());

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
      getView().setPreviewPhoto(photo.photoUrl());
   }

   private void onImageCropped(String path) {
      smartCardUserDataInteractor.smartCardAvatarPipe().send(SmartCardAvatarCommand.fromSchemePath(path));
   }

   private boolean isDataChanged() {
      return profileDataIsChanged;
   }

   private void observeChanging() {
      Screen view = getView();
      //noinspection all
      Observable.combineLatest(
            smartCardInteractor.smartCardUserPipe().observeSuccessWithReplay()
                  .map(Command::getResult),

            view.firstNameObservable(),
            view.middleNameObservable(),
            view.codeObservable(),
            view.phoneObservable(),
            smartCardUserDataInteractor.smartCardAvatarPipe()
                  .observeSuccess()
                  .map(Command::getResult)
                  .startWith(Observable.just(null)),
            (user, firstName, middleName, code, phone, newAvatar) -> handleFieldChanges(user, firstName, middleName, code, phone, newAvatar))
            .compose(bindView())
            .subscribe();
   }

   private Observable<Void> handleFieldChanges(SmartCardUser user, String firstName, String middleName, String code, String phone, SmartCardUserPhoto newAvatar) {
      if (!user.firstName().equals(firstName)) {
         profileDataIsChanged = true;
         return null;
      }
      if (!user.middleName().equals(middleName)) {
         profileDataIsChanged = true;
         return null;
      }
      if ((user.userPhoto() == null && newAvatar != null) ||
            (user.userPhoto() != null && newAvatar != null && !newAvatar.original().equals(user.userPhoto().original()))) {
         return null;
      }
      if (user.phoneNumber() == null && !code.isEmpty() && !phone.isEmpty()) {
         profileDataIsChanged = true;
         return null;
      }
      if (user.phoneNumber() != null && !user.phoneNumber().code().equals(code)) {
         profileDataIsChanged = true;
         return null;
      }
      if (user.phoneNumber() != null && !user.phoneNumber().number().equals(phone)) {
         profileDataIsChanged = true;
         return null;
      }
      profileDataIsChanged = false;
      return null;
   }

   public interface Screen extends WalletScreen {

      void pickPhoto();

      void hidePhotoPicker();

      void cropPhoto(Uri photoPath);

      Observable<Uri> observePickPhoto();

      Observable<String> observeCropper();

      void setPreviewPhoto(String photoUrl);

      void setUserName(String firstName, String middleName, String lastName);

      void setPhone(String countryCode, String number);

      void showRevertChangesDialog();

      String getFirstName();

      String getMiddleName();

      String getLastName();

      String getCountryCode();

      String getPhoneNumber();

      Observable<String> firstNameObservable();

      Observable<String> middleNameObservable();

      Observable<String> codeObservable();

      Observable<String> phoneObservable();

      void showError(Throwable throwable);

      void showUploadServerError();

      void showNetworkUnavailableError();

      void showProgress();

      void hideProgress();
   }
}
