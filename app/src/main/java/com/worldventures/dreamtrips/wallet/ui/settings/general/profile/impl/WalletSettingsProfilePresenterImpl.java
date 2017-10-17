package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.impl;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardProfileAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfileScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhone;
import static com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhoto;

public class WalletSettingsProfilePresenterImpl extends WalletPresenterImpl<WalletSettingsProfileScreen> implements WalletSettingsProfilePresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final SmartCardUserDataInteractor smartCardUserDataInteractor;
   private final WalletSocialInfoProvider socialInfoProvider;
   private final WalletProfileDelegate delegate;

   private SmartCardUser user;

   public WalletSettingsProfilePresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WalletAnalyticsInteractor analyticsInteractor,
         SmartCardUserDataInteractor smartCardUserDataInteractor, WalletSocialInfoProvider socialInfoProvider) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.smartCardUserDataInteractor = smartCardUserDataInteractor;
      this.socialInfoProvider = socialInfoProvider;
      this.delegate = new WalletProfileDelegate(smartCardUserDataInteractor, analyticsInteractor);
   }

   @Override
   public void attachView(WalletSettingsProfileScreen view) {
      super.attachView(view);

      fetchProfile();
      observeChangeFields(view);

      delegate.observeProfileUploading(view, this::applyChanges, throwable -> view.setDoneButtonEnabled(isDataChanged()));
      delegate.observePickerAndCropper(view);
      delegate.sendAnalytics(new SmartCardProfileAction());

   }

   private void observeChangeFields(WalletSettingsProfileScreen view) {
      view.setDoneButtonEnabled(isDataChanged());
      view.observeChangesProfileFields()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(changed -> view.setDoneButtonEnabled(isDataChanged()));
   }

   private void fetchProfile() {
      smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(command -> setUser(command.getResult()), throwable -> Timber.e(throwable, ""));
   }

   @SuppressWarnings("ConstantConditions")
   private void setUser(SmartCardUser user) {
      this.user = user;
      if (getView().getUser().isEmpty()) {
         getView().setUser(delegate.toViewModel(user));
      }
      getView().setDoneButtonEnabled(isDataChanged());
   }

   @Override
   public void handleDoneAction() {
      assertSmartCardConnected(() -> saveUserProfile(false));
   }

   @Override
   public void confirmDisplayTypeChange() {
      assertSmartCardConnected(() -> saveUserProfile(true));
   }

   @Override
   public void handleBackOnDataChanged() {
      getView().showRevertChangesDialog();
   }

   @Override
   public void revertChanges() {
      if (user != null) {
         getView().setUser(delegate.toViewModel(user));
      }
      goBack();
   }

   private void applyChanges(SmartCardUser updatedUser) {
      this.user = updatedUser;
      getView().setUser(delegate.toViewModel(updatedUser));
      goBack();
   }

   @SuppressWarnings("ConstantConditions")
   private void saveUserProfile(boolean forceUpdateDisplayType) {
      if (isDataChanged()) {
         getView().setDoneButtonEnabled(false);
         final ProfileViewModel profile = getView().getUser();

         final ChangedFields changedFields = ImmutableChangedFields.builder()
               .firstName(profile.getFirstName())
               .middleName(profile.getMiddleName())
               .lastName(profile.getLastNameWithSuffix())
               .phone(delegate.createPhone(profile))
               .photo(delegate.createPhoto(profile))
               .build();

         smartCardUserDataInteractor.updateSmartCardUserPipe()
               .send(new UpdateSmartCardUserCommand(changedFields, forceUpdateDisplayType));
      }
   }

   @SuppressWarnings("ConstantConditions")
   @Override
   public boolean isDataChanged() {
      final ProfileViewModel profile = getView().getUser();
      return user != null
            && !(equalsPhoto(user.userPhoto(), profile.getChosenPhotoUri())
            && profile.getFirstName().equals(user.firstName())
            && profile.getMiddleName().equals(user.middleName())
            && profile.getLastNameWithSuffix().equals(user.lastName())
            && equalsPhone(user.phoneNumber(), profile.getPhoneCode(), profile.getPhoneNumber()));
   }

   @Override
   public void goBack() {
      Timber.d("WTF :: navigateBack");
      getNavigator().goBack();
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
      assertSmartCardConnected(() -> {
         final ProfileViewModel profileViewModel = isDataChanged() ? getView().getUser() : null;
         try {
            if (profileViewModel != null) {
               WalletValidateHelper.validateUserFullNameOrThrow(profileViewModel.getFirstName(), profileViewModel.getMiddleName(), profileViewModel
                     .getLastNameWithSuffix());
            }
            getNavigator().goSettingsDisplayOptions(DisplayOptionsSource.PROFILE, profileViewModel);
         } catch (FormatException e) {
            getView().provideUpdateSmartCardOperation(delegate).showError(null, e);
         }
      });
   }

   @SuppressWarnings("ConstantConditions")
   private void assertSmartCardConnected(Action0 onConnected) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> {
                     if (command.getResult().connectionStatus().isConnected()) {
                        onConnected.call();
                     } else {
                        getView().showSCNonConnectionDialog();
                     }
                  })
            );
   }
}
