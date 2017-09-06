package com.worldventures.dreamtrips.wallet.ui.wizard.profile.impl;


import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.SetupUserAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePresenter;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfileScreen;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.smartcard.action.user.RemoveUserPhotoAction;

public class WizardEditProfilePresenterImpl extends WalletPresenterImpl<WizardEditProfileScreen> implements WizardEditProfilePresenter {

   private final WizardInteractor wizardInteractor;
   private final WalletAnalyticsInteractor analyticsInteractor;
   private final WalletSocialInfoProvider socialInfoProvider;
   private final WalletFeatureHelper featureHelper;

   private final WalletProfileDelegate delegate;

   public WizardEditProfilePresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService, WizardInteractor wizardInteractor, WalletAnalyticsInteractor analyticsInteractor,
         WalletSocialInfoProvider socialInfoProvider, SmartCardUserDataInteractor smartCardUserDataInteractor, WalletFeatureHelper featureHelper) {
      super(navigator, smartCardInteractor, networkService);
      this.wizardInteractor = wizardInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.socialInfoProvider = socialInfoProvider;
      this.featureHelper = featureHelper;
      this.delegate = new WalletProfileDelegate(smartCardUserDataInteractor, analyticsInteractor);
   }

   @Override
   public void attachView(WizardEditProfileScreen view) {
      super.attachView(view);
      if (getView().getProfile().isEmpty()) {
         attachProfile(getView());
      }
      observeSetupUserCommand(getView());

      delegate.observePickerAndCropper(getView());
      delegate.sendAnalytics(new SetupUserAction());
   }

   private void observeSetupUserCommand(WizardEditProfileScreen view) {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideOperationView())
                  .onSuccess(command -> onUserSetupSuccess(command.getResult()))
                  .create());
   }

   private void onUserSetupSuccess(SmartCardUser user) {
      analyticsInteractor.walletAnalyticsPipe()
            .send(new WalletAnalyticsCommand(
                  user.userPhoto() != null ? PhotoWasSetAction.methodDefault() : PhotoWasSetAction.noPhoto())
            );
      featureHelper.navigateFromSetupUserScreen(getNavigator());
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
      WalletProfileUtils.checkUserNameValidation(profile.getFirstName(), profile.getMiddleName(), profile.getLastNameWithSuffix(),
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
         getSmartCardInteractor().removeUserPhotoActionPipe()
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
