package com.worldventures.wallet.ui.wizard.profile.impl;

import com.worldventures.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.wallet.analytics.wizard.SetupUserAction;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.SetupUserDataCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileUtils;
import com.worldventures.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.wallet.ui.wizard.profile.WizardEditProfileScreen;
import com.worldventures.wallet.util.WalletFilesUtils;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.smartcard.action.user.RemoveUserPhotoAction;
import rx.android.schedulers.AndroidSchedulers;

public class WizardEditProfilePresenterImpl extends WalletPresenterImpl<WizardEditProfileScreen> implements WizardEditProfilePresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final WizardInteractor wizardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final WalletSocialInfoProvider socialInfoProvider;

   private final WalletProfileDelegate delegate;

   public WizardEditProfilePresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, WizardInteractor wizardInteractor, WalletAnalyticsInteractor analyticsInteractor,
         WalletSocialInfoProvider socialInfoProvider, SmartCardUserDataInteractor smartCardUserDataInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.wizardInteractor = wizardInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.socialInfoProvider = socialInfoProvider;
      this.delegate = new WalletProfileDelegate(smartCardUserDataInteractor, smartCardInteractor, analyticsInteractor);
   }

   @Override
   public void attachView(WizardEditProfileScreen view) {
      super.attachView(view);
      if (getView().getProfile().isEmpty()) {
         attachProfile(getView());
      }
      observeSetupUserCommand(getView());

      delegate.sendAnalytics(new SetupUserAction());
   }

   private void observeSetupUserCommand(WizardEditProfileScreen view) {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideOperationView())
                  .onSuccess(command -> onUserSetupSuccess(command.getResult()))
                  .create());
   }

   private void onUserSetupSuccess(SmartCardUser user) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(
                  user.getUserPhoto() != null ? PhotoWasSetAction.Companion.methodDefault() : PhotoWasSetAction.Companion
                        .noPhoto())
            );
      if (getView().getProvisionMode() != null) {
         getNavigator().goWizardAssignUser(getView().getProvisionMode());
      }
   }

   private void attachProfile(WizardEditProfileScreen view) {
      view.setProfile(delegate.toViewModel(
            socialInfoProvider.firstName(),
            socialInfoProvider.lastName(),
            socialInfoProvider.photoThumb()
      ));
   }

   @Override
   public void back() {
      getNavigator().goBack();
   }

   @SuppressWarnings("ConstantConditions")
   @Override
   public void choosePhoto() {
      getView().pickPhoto(delegate.provideInitialPhotoUrl(socialInfoProvider.photoThumb()));
   }

   @Override
   public void setupUserData() {
      final WizardEditProfileScreen view = getView();
      // noinspection ConstantConditions
      final ProfileViewModel profile = view.getProfile();
      //noinspection ConstantConditions
      WalletProfileUtils.INSTANCE.checkUserNameValidation(profile.getFirstName(), profile.getMiddleName(), profile.getLastName(),
            () -> view.showConfirmationDialog(profile),
            e -> view.provideOperationView().showError(null, e));
   }

   @Override
   public void onUserDataConfirmed() {
      // noinspection ConstantConditions
      final ProfileViewModel profile = getView().getProfile();
      final SmartCardUser smartCardUser = delegate.createSmartCardUser(profile);
      wizardInteractor.setupUserDataPipe().send(new SetupUserDataCommand(smartCardUser));
      if (profile.isPhotoEmpty()) {
         smartCardInteractor.removeUserPhotoActionPipe()
               .send(new RemoveUserPhotoAction());
      }
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
}
