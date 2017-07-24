package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.impl;


import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.analytics.settings.ProfileChangesSavedAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardProfileAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfileScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.functions.Action0;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhone;
import static com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhoto;

public class WalletSettingsProfilePresenterImpl extends WalletPresenterImpl<WalletSettingsProfileScreen> implements WalletSettingsProfilePresenter {

   private final SmartCardUserDataInteractor smartCardUserDataInteractor;
   private final WalletSocialInfoProvider socialInfoProvider;
   private final WalletProfileDelegate delegate;

   private SmartCardUser user;

   public WalletSettingsProfilePresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, AnalyticsInteractor analyticsInteractor,
         SmartCardUserDataInteractor smartCardUserDataInteractor, WalletSocialInfoProvider socialInfoProvider) {
      super(navigator, smartCardInteractor, networkService);
      this.smartCardUserDataInteractor = smartCardUserDataInteractor;
      this.socialInfoProvider = socialInfoProvider;
      this.delegate = new WalletProfileDelegate(analyticsInteractor);
   }

   @Override
   public void attachView(WalletSettingsProfileScreen view) {
      super.attachView(view);

      fetchProfile();
      observeUploading(view);
      observeChangeFields(view);

      delegate.observePickerAndCropper(view);
      delegate.sendAnalytics(new SmartCardProfileAction());

   }

   private void observeChangeFields(WalletSettingsProfileScreen view) {
      view.setDoneButtonEnabled(isDataChanged());
      view.observeChangesProfileFields()
            .compose(bindViewIoToMainComposer())
            .subscribe(changed -> view.setDoneButtonEnabled(isDataChanged()));
   }

   private void fetchProfile() {
      getSmartCardInteractor().smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> setUser(command.getResult()), throwable -> Timber.e(throwable, ""));
   }

   @SuppressWarnings("ConstantConditions")
   private void setUser(SmartCardUser user) {
      this.user = user;
      getView().setUser(delegate.toViewModel(user));
   }

   private void observeUploading(WalletSettingsProfileScreen view) {
      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.updateSmartCardUserPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideUpdateSmartCardOperation())
                  .onSuccess(setupUserDataCommand -> {
                     delegate.sendAnalytics(new ProfileChangesSavedAction());
                     goBack();
                  })
                  .onFail((command, throwable) -> view.setDoneButtonEnabled(isDataChanged()))
                  .create());

      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.retryHttpUploadUpdatingPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideHttpUploadOperation()).create());
   }

   @Override
   public void handleDoneAction() {
      assertSmartCardConnected(() -> saveUserProfile(false));
   }

   @Override
   public void confirmDisplayTypeChange() {
      assertSmartCardConnected(() -> saveUserProfile(true));
   }

   @SuppressWarnings("ConstantConditions")
   private void saveUserProfile(boolean forceUpdateDisplayType) {
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

         smartCardUserDataInteractor.updateSmartCardUserPipe()
               .send(new UpdateSmartCardUserCommand(changedFields, forceUpdateDisplayType));
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
   @Override
   public void handleBackAction() {
      if (isDataChanged()) {
         getView().showRevertChangesDialog();
      } else {
         goBack();
      }
   }

   @Override
   public void cancelUploadServerUserData() {
      smartCardUserDataInteractor.revertSmartCardUserUpdatingPipe().send(new RevertSmartCardUserUpdatingCommand());
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void retryUploadToServer() {
      smartCardUserDataInteractor.retryHttpUploadUpdatingPipe().send(new RetryHttpUploadUpdatingCommand());
   }

   @SuppressWarnings("ConstantConditions")
   @Override
   public void choosePhoto() {
      getView().pickPhoto(delegate.provideInitialPhotoUrl(socialInfoProvider.photoThumb()));
   }

   @SuppressWarnings("ConstantConditions")
   @Override
   public void doNotAdd() {
      getView().dropPhoto();
   }

   @SuppressWarnings("ConstantConditions")
   @Override
   public void handlePickedPhoto(PhotoPickerModel model) {
      getView().cropPhoto(WalletFilesUtils.convertPickedPhotoToUri(model));
   }

   @SuppressWarnings("ConstantConditions")
   @Override
   public void openDisplaySettings() {
      assertSmartCardConnected(() -> getNavigator().goSettingsDisplayOptions(DisplayOptionsSource.PROFILE,
            delegate.createSmartCardUser(getView().getUser())));
   }

   @SuppressWarnings("ConstantConditions")
   private void assertSmartCardConnected(Action0 onConnected) {
      getSmartCardInteractor().deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> {
                     if (command.getResult().connectionStatus().isConnected()) onConnected.call();
                     else getView().showSCNonConnectionDialog();
                  })
            );
   }
}
