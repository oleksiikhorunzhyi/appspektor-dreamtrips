package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardProfileAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.device.DeviceStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.UpdateSmartCardUserView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action0;
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
   @Inject WalletSocialInfoProvider socialInfoProvider;
   @Inject ErrorHandlerFactory errorHandlerFactory;

   private final WalletProfileDelegate delegate;
   private SmartCardUser user;

   private BackStackDelegate.BackPressedListener systemBackPressedListener = () -> {
      handleBackAction();
      return true;
   };

   public WalletSettingsProfilePresenter(Context context, Injector injector) {
      super(context, injector);
      this.delegate = new WalletProfileDelegate(smartCardUserDataInteractor, analyticsInteractor);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      backStackDelegate.addListener(systemBackPressedListener);

      fetchProfile();
      observeChangeFields(view);

      delegate.observeProfileUploading(view, this::goBack, throwable -> view.setDoneButtonEnabled(isDataChanged()));
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
      if (getView().getUser().isEmpty()) {
         getView().setUser(delegate.toViewModel(user));
      }
   }

   void handleDoneAction() {
      assertSmartCardConnected(() -> saveUserProfile(false));
   }

   void confirmDisplayTypeChange() {
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
   void handleBackAction() {
      if (isDataChanged()) {
         getView().showRevertChangesDialog();
      } else {
         goBack();
      }
   }

   void goBack() {
      navigator.goBack();
   }

   @SuppressWarnings("ConstantConditions")
   void choosePhoto() {
      getView().pickPhoto(delegate.provideInitialPhotoUrl(socialInfoProvider.photoThumb()));
   }

   @SuppressWarnings("ConstantConditions")
   void doNotAdd() {
      getView().dropPhoto();
   }

   @SuppressWarnings("ConstantConditions")
   void handlePickedPhoto(PhotoPickerModel model) {
      getView().cropPhoto(WalletFilesUtils.convertPickedPhotoToUri(model));
   }

   @Override
   public void detachView(boolean retainInstance) {
      backStackDelegate.removeListener(systemBackPressedListener);
      super.detachView(retainInstance);
   }

   @SuppressWarnings("ConstantConditions")
   void openDisplaySettings() {
      assertSmartCardConnected(() -> {
         final SmartCardUser user = isDataChanged() ? delegate.createSmartCardUser(getView().getUser()) : null;
         try {
            if (user != null) {
               WalletValidateHelper.validateUserFullNameOrThrow(user.firstName(), user.middleName(), user.lastName());
            }
            navigator.go(new DisplayOptionsSettingsPath(DisplayOptionsSource.PROFILE, user));
         } catch (FormatException e) {
            getView().provideUpdateSmartCardOperation(delegate).showError(null, e);
         }
      });
   }

   @SuppressWarnings("ConstantConditions")
   private void assertSmartCardConnected(Action0 onConnected) {
      smartCardInteractor.deviceStatePipe()
            .createObservable(DeviceStateCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<DeviceStateCommand>()
                  .onSuccess(command -> {
                     if (command.getResult().connectionStatus().isConnected()) onConnected.call();
                     else getView().showSCNonConnectionDialog();
                  })
            );
   }

   public interface Screen extends WalletScreen, WalletProfilePhotoView, UpdateSmartCardUserView {

      void setUser(ProfileViewModel model);

      ProfileViewModel getUser();

      void showRevertChangesDialog();

      void setDoneButtonEnabled(boolean enable);

      void showSCNonConnectionDialog();

      PublishSubject<ProfileViewModel> observeChangesProfileFields();
   }
}
