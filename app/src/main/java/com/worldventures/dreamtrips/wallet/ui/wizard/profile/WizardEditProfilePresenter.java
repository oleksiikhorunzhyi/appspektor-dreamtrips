package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.SetupUserAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalAction;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.proposal.PinProposalPath;

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
   @Inject SessionHolder<UserSession> appSessionHolder;

   private final WalletProfileDelegate delegate;

   public WizardEditProfilePresenter(Context context, Injector injector) {
      super(context, injector);
      this.delegate = new WalletProfileDelegate(analyticsInteractor);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      attachProfile(view);
      observeSetupUserCommand(view);

      delegate.observePickerAndCropper(view);
      delegate.sendAnalytics(new SetupUserAction());
      delegate.setupInputMode(activity);
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
      navigator.go(new PinProposalPath(PinProposalAction.WIZARD));
   }

   private void attachProfile(Screen view) {
      view.setProfile(delegate.toViewModel(appSessionHolder.get().get().getUser()));
   }

   void back() {
      getView().hidePhotoPicker();
      navigator.goBack();
   }

   void choosePhoto() {
      getView().pickPhoto();
   }

   void setupInputMode() {
      delegate.setupInputMode(activity);
   }

   void setupUserData() {
      final Screen view = getView();
      // noinspection ConstantConditions
      final ProfileViewModel profile = view.getProfile();
      //noinspection ConstantConditions
      WalletProfileUtils.checkUserNameValidation(profile.getFirstName(), profile.getMiddleName(), profile.getLastName(),
            () -> view.showConfirmationDialog(profile.getFirstName(), profile.getLastName()),
            e -> view.provideOperationView().showError(null, e));
   }

   void onUserDataConfirmed() {
      // noinspection ConstantConditions
      final ProfileViewModel profile = getView().getProfile();
      wizardInteractor.setupUserDataPipe()
            .send(new SetupUserDataCommand(
                  ImmutableSmartCardUser.builder()
                        .firstName(profile.getFirstName())
                        .middleName(profile.getMiddleName())
                        .lastName(profile.getLastName())
                        .phoneNumber(delegate.createPhone(profile))
                        .userPhoto(delegate.createPhoto(profile))
                        .build()
            ));
      if (profile.isPhotoEmpty()) {
         smartCardInteractor.removeUserPhotoActionPipe()
               .send(new RemoveUserPhotoAction());
      }
   }

   public void dontAdd() {
      getView().hidePhotoPicker();
      getView().dropPhoto();
   }

   public interface Screen extends WalletScreen, WalletProfilePhotoView {

      OperationView<SetupUserDataCommand> provideOperationView();

      void setProfile(ProfileViewModel model);

      ProfileViewModel getProfile();

      void showConfirmationDialog(String firstName, String lastName);
   }
}
