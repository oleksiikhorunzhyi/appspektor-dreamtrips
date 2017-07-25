package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.SetupUserAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandlerFactory;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;
import com.worldventures.dreamtrips.wallet.util.WalletFilesUtils;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.user.RemoveUserPhotoAction;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject WalletSocialInfoProvider socialInfoProvider;
   @Inject ErrorHandlerFactory errorHandlerFactory;
   @Inject WalletFeatureHelper featureHelper;

   private final WalletProfileDelegate delegate;

   public WizardEditProfilePresenter(Context context, Injector injector) {
      super(context, injector);
      this.delegate = new WalletProfileDelegate(smartCardUserDataInteractor, analyticsInteractor);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (getView().getProfile().isEmpty()) {
         attachProfile(getView());
      }
      observeSetupUserCommand(getView());

      delegate.observePickerAndCropper(getView());
      delegate.sendAnalytics(new SetupUserAction());
   }

   private void observeSetupUserCommand(Screen view) {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideOperationView())
                  .onSuccess(command -> onUserSetupSuccess(command.getResult()))
                  .create());
   }

   private void onUserSetupSuccess(SmartCardUser user) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(
                  user.userPhoto() != null ? PhotoWasSetAction.methodDefault() : PhotoWasSetAction.noPhoto())
            );
      featureHelper.navigateFromSetupUserScreen(navigator, user, false);
   }

   private void attachProfile(Screen view) {
      view.setProfile(delegate.toViewModel(
            socialInfoProvider.firstName(),
            socialInfoProvider.lastName(),
            socialInfoProvider.photoThumb()
      ));
   }

   void back() {
      navigator.goBack();
   }

   @SuppressWarnings("ConstantConditions")
   void choosePhoto() {
      getView().pickPhoto(delegate.provideInitialPhotoUrl(socialInfoProvider.photoThumb()));
   }

   void setupUserData() {
      final Screen view = getView();
      // noinspection ConstantConditions
      final ProfileViewModel profile = view.getProfile();
      //noinspection ConstantConditions
      WalletProfileUtils.checkUserNameValidation(profile.getFirstName(), profile.getMiddleName(), profile.getLastName(),
            () -> view.showConfirmationDialog(profile),
            e -> view.provideOperationView().showError(null, e));
   }

   void onUserDataConfirmed() {
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
   void doNotAdd() {
      getView().dropPhoto();
   }

   @SuppressWarnings("ConstantConditions")
   void handlePickedPhoto(PhotoPickerModel model) {
      getView().cropPhoto(WalletFilesUtils.convertPickedPhotoToUri(model));
   }

   public interface Screen extends WalletScreen, WalletProfilePhotoView {

      OperationView<SetupUserDataCommand> provideOperationView();

      void setProfile(ProfileViewModel model);

      ProfileViewModel getProfile();

      void showConfirmationDialog(ProfileViewModel profileViewModel);
   }
}
