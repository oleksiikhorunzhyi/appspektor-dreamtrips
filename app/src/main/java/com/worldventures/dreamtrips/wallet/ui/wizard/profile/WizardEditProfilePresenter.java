package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.wizard.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.analytics.wizard.SetupUserAction;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhoneScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.enter.EnterPinPath;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
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

      delegate.sendAnalytics(new SetupUserAction());
      delegate.setupInputMode(activity);
   }

   private void observeSetupUserCommand(Screen view) {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideOperationView())
                  .onSuccess(command -> onUserSetupSuccess())
                  .create());
   }

   private void onUserSetupSuccess() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new PhotoWasSetAction()));
      navigator.go(new EnterPinPath(Action.SETUP));
   }

   private void attachProfile(Screen view) {
      final User userProfile = appSessionHolder.get().get().getUser();
      view.setUserFullName(userProfile.getFirstName(), userProfile.getLastName());

      final String defaultUserAvatar = userProfile.getAvatar().getThumb();
      if (!ProjectTextUtils.isEmpty(defaultUserAvatar)) {
         delegate.setPhotoUri(defaultUserAvatar, view);
      }
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
      //noinspection ConstantConditions
      WalletProfileUtils.checkUserNameValidation(view.getFirstName(), view.getMiddleName(), view.getLastName(),
            () -> view.showConfirmationDialog(view.getFirstName(), view.getLastName()),
            e -> view.provideOperationView().showError(null, e));
   }

   void onUserDataConfirmed() {
      wizardInteractor.setupUserDataPipe()
            .send(new SetupUserDataCommand(
                  ImmutableSmartCardUser.builder()
                        .firstName(getView().getFirstName())
                        .middleName(getView().getFirstName())
                        .lastName(getView().getLastName())
                        .phoneNumber(getView().userPhone())
                        .userPhoto(delegate.preparedPhoto())
                        .build()
            ));
   }

   public interface Screen extends WalletProfilePhoneScreen, WalletProfilePhotoView {

      OperationView<SetupUserDataCommand> provideOperationView();

      void setUserFullName(String firstName, String lastName);

      String getFirstName();

      String getMiddleName();

      String getLastName();

      void showConfirmationDialog(String firstName, String lastName);}
}
