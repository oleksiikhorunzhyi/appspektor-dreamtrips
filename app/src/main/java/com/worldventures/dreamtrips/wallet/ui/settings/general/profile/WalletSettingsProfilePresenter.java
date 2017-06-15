package com.worldventures.dreamtrips.wallet.ui.settings.general.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;

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
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfilePhotoView;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.user.RemoveUserPhotoAction;
import rx.functions.Action1;
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

      delegate.setupInputMode(activity);
      delegate.observePickerAndCropper(view);
      delegate.sendAnalytics(new SmartCardProfileAction());
   }

   private void fetchProfile() {
      smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .compose(bindViewIoToMainComposer())
            .subscribe(command -> setUser(command.getResult()), throwable -> Timber.e(throwable, ""));
   }

   private void setUser(SmartCardUser user) {
      this.user = user;
      //noinspection ConstantConditions
      getView().setUser(delegate.toViewModel(user));
   }

   private void observeUploading(Screen view) {
      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(smartCardUserDataInteractor.updateSmartCardUserPipe()))
            .subscribe(OperationActionSubscriber.forView(view.provideUpdateSmartCardOperation())
                  .onSuccess(new Action1<UpdateSmartCardUserCommand>() {
                     @Override
                     public void call(UpdateSmartCardUserCommand setupUserDataCommand) {
                        WalletSettingsProfilePresenter.this.goBack();
                     }
                  })
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

   private boolean isDataChanged() {
      //noinspection ConstantConditions
      final ProfileViewModel profile = getView().getUser();
      return !(equalsPhoto(user.userPhoto(), profile.getChosenPhotoUri()) &&
            profile.getFirstName().equals(user.firstName()) &&
            profile.getMiddleName().equals(user.middleName()) &&
            profile.getLastName().equals(user.lastName()) &&
            equalsPhone(user.phoneNumber(), profile.getPhoneCode(), profile.getPhoneNumber()));
   }

   void handleBackAction() {
      if (isDataChanged()) {
         //noinspection ConstantConditions
         getView().showRevertChangesDialog();
      } else {
         //noinspection ConstantConditions
         getView().hidePhotoPicker();
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

   public void dontAdd() {
      getView().hidePhotoPicker();
      getView().dropPhoto();
   }

   public interface Screen extends WalletScreen, WalletProfilePhotoView {

      void setUser(ProfileViewModel model);

      ProfileViewModel getUser();

      void showRevertChangesDialog();

      OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation();

      OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation();
   }
}
