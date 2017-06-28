package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.settings.ProfileChangesSavedAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardProfileAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ConnectionStatus;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.user.RemoveUserPhotoAction;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhone;
import static com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhoto;

public class WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfilePresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject BackStackDelegate backStackDelegate;
   @Inject SessionHolder<UserSession> appSessionHolder;
   @Inject ErrorHandlerFactory errorHandlerFactory;

   private final WalletProfileDelegate delegate;
   private SmartCardUser user;

   private BackStackDelegate.BackPressedListener systemBackPressedListener = () -> {
      handleBackAction();
      return true;
   };

   public WalletSettingsProfilePresenter(Context context, Injector injector) {
      super(context, injector);
      this.delegate = new WalletProfileDelegate(analyticsInteractor);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      backStackDelegate.addListener(systemBackPressedListener);

      fetchProfile();
      observeUploading(view);
      observeChangeFields(view);

      delegate.setupInputMode(activity);
      delegate.observePickerAndCropper(view);
      delegate.sendAnalytics(new SmartCardProfileAction());
   }

   private void observeChangeFields(Screen view) {
      view.setDoneButtonEnabled(isDataChanged());
      view.observeChangesProfileFields()
            .compose(bindViewIoToMainComposer())
            .subscribe(changed -> view.setDoneButtonEnabled(isDataChanged()));
   }

   private void fetchProfile() {
      smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> setUser(command.getResult()), throwable -> Timber.e(throwable, ""));
   }

   @SuppressWarnings("ConstantConditions")
   private void setUser(SmartCardUser user) {
      this.user = user;
      getView().setUser(delegate.toViewModel(user));
   }

   private void observeUploading(Screen view) {
      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.updateSmartCardUserPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideUpdateSmartCardOperation())
                  .onSuccess(setupUserDataCommand -> goBack())
                  .create());

      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideHttpUploadOperation()).create());
   }

   void setupInputMode() {
      delegate.setupInputMode(activity);
   }

   @SuppressWarnings("ConstantConditions")
   void handleDoneAction() {
      if (isDataChanged()) {
         getView().setDoneButtonEnabled(false);
         final ProfileViewModel profile = getView().getUser();

         final ChangedFields changedFields = ImmutableChangedFields.builder()
               .firstName(profile.getFirstName())
               .middleName(profile.getMiddleName())
               .lastName(profile.getLastName())
               .phone(delegate.createPhone(profile))
               .photo(delegate.createPhoto(profile))
               .build();

         smartCardUserDataInteractor.updateSmartCardUserPipe().send(new UpdateSmartCardUserCommand(changedFields));

         if (profile.isPhotoEmpty()) {
            smartCardInteractor.removeUserPhotoActionPipe().send(new RemoveUserPhotoAction());
         }
         delegate.sendAnalytics(new ProfileChangesSavedAction());
      }
   }

   @SuppressWarnings("ConstantConditions")
   private boolean isDataChanged() {
      final ProfileViewModel profile = getView().getUser();
      return user != null &&
            !(equalsPhoto(user.userPhoto(), profile.getChosenPhotoUri()) &&
                  profile.getFirstName().equals(user.firstName()) &&
                  profile.getMiddleName().equals(user.middleName()) &&
                  profile.getLastName().equals(user.lastName()) &&
                  equalsPhone(user.phoneNumber(), profile.getPhoneCode(), profile.getPhoneNumber()));
   }

   @SuppressWarnings("ConstantConditions")
   void handleBackAction() {
      if (isDataChanged()) {
         getView().showRevertChangesDialog();
      } else {
         goBack();
      }
   }

   void cancelUploadServerUserData() {
      smartCardUserDataInteractor.revertSmartCardUserUpdatingPipe().send(new RevertSmartCardUserUpdatingCommand());
   }

   void goBack() {
      navigator.goBack();
   }

   void retryUploadToServer() {
      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe().send(new RetryHttpUploadUpdatingCommand());
   }

   @SuppressWarnings("ConstantConditions")
   void choosePhoto() {
      getView().pickPhoto(delegate.provideInitialPhotoUrl(appSessionHolder.get().get().getUser()));
   }

   @SuppressWarnings("ConstantConditions")
   void doNotAdd() {
      getView().dropPhoto();
   }

   @SuppressWarnings("ConstantConditions")
   void handlePickedPhoto(BasePickerViewModel model) {
      getView().cropPhoto(WalletFilesUtils.convertPickedPhotoToUri(model));
   }

   @Override
   public void detachView(boolean retainInstance) {
      backStackDelegate.removeListener(systemBackPressedListener);
      super.detachView(retainInstance);
   }

   void openDisplaySettings() {
      fetchConnectionStatus(connectionStatus -> {
         if (connectionStatus.isConnected()) {
            navigator.go(new DisplayOptionsSettingsPath(DisplayOptionsSource.PROFILE,
                  delegate.createSmartCardUser(getView().getUser())));
         } else {
            getView().showSCNonConnectionDialog();
         }
      });
   }

   private void fetchConnectionStatus(Action1<ConnectionStatus> action) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> action.call(command.getResult().connectionStatus()))
            );
   }

   public interface Screen extends WalletScreen, WalletProfilePhotoView {

      void setUser(ProfileViewModel model);

      ProfileViewModel getUser();

      void showRevertChangesDialog();

      OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation();

      OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation();

      void setDoneButtonEnabled(boolean enable);

      PublishSubject<ProfileViewModel> observeChangesProfileFields();

      void showSCNonConnectionDialog();
   }
}
