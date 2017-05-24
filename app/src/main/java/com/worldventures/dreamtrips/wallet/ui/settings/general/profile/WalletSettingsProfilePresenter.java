package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.settings.ProfileChangesSavedAction;
import com.worldventures.dreamtrips.wallet.analytics.settings.SmartCardProfileAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.RevertSmartCardUserUpdatingCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhoneScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileUtils.equalsPhone;

public class WalletSettingsProfilePresenter extends WalletPresenter<WalletSettingsProfilePresenter.Screen, WalletSettingsProfileState> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject BackStackDelegate backStackDelegate;

   private final WalletProfileDelegate delegate;

   private BackStackDelegate.BackPressedListener systemBackPressedListener = () -> {
      handleBackAction();
      return true;
   };

   public WalletSettingsProfilePresenter(Context context, Injector injector) {
      super(context, injector);
      this.delegate = new WalletProfileDelegate(analyticsInteractor);
   }

   // View State // TODO: 5/24/17
   @Override
   public void onNewViewState() {
      state = new WalletSettingsProfileState();
   }

   @Override
   public void applyViewState() {
      super.applyViewState();
//      delegate.setPreparedPhoto(state.getUserPhoto());
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
//      state.setUserPhoto(delegate.preparedPhoto());
      super.onSaveInstanceState(bundle);
   }

   // bind to view
   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      backStackDelegate.addListener(systemBackPressedListener);

      fetchProfile(view);
      observeUploading(view);

      delegate.setupInputMode(activity);
      delegate.observePickerAndCropper(view);
      delegate.sendAnalytics(new SmartCardProfileAction());
   }

   private void fetchProfile(Screen view) {
      smartCardInteractor.smartCardUserPipe().createObservableResult(SmartCardUserCommand.fetch())
            .map(Command::getResult)
            .compose(bindViewIoToMainComposer())
            .subscribe(view::setUser, throwable -> Timber.e(throwable, ""));
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

   void handleDoneAction() {
      if (isDataChanged()) {
         //noinspection ConstantConditions
         final ChangedFields changedFields = ImmutableChangedFields.builder()
               .firstName(getView().getFirstName())
               .middleName(getView().getMiddleName())
               .lastName(getView().getLastName())
               .phone(getView().userPhone())
               .photo(delegate.preparedPhoto())
               .build();

         smartCardUserDataInteractor.updateSmartCardUserPipe().send(new UpdateSmartCardUserCommand(changedFields));
         delegate.sendAnalytics(new ProfileChangesSavedAction());
      }
   }

   private boolean isDataChanged() {
      //noinspection ConstantConditions
      final SmartCardUser user = getView().getCurrentUser();
      return delegate.preparedPhoto() != null ||
            user.firstName().equals(getView().getFirstName()) ||
            user.middleName().equals(getView().getMiddleName()) ||
            user.lastName().equals(getView().getLastName()) ||
            equalsPhone(user.phoneNumber(), getView().userPhone());
   }

   void handleBackAction() {
      if (isDataChanged()) {
         //noinspection ConstantConditions
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

   void choosePhoto() {
      //noinspection ConstantConditions
      getView().pickPhoto();
   }

   @Override
   public void detachView(boolean retainInstance) {
      backStackDelegate.removeListener(systemBackPressedListener);
      super.detachView(retainInstance);
   }

   public interface Screen extends WalletProfilePhoneScreen, WalletProfilePhotoView {
      void setUser(SmartCardUser user);

      void showRevertChangesDialog();

      String getFirstName();

      String getMiddleName();

      String getLastName();

      SmartCardUser getCurrentUser();

      OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation();

      OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation();
   }
}
