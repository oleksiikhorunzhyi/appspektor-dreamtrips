package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.analytics.PhotoWasSetAction;
import com.worldventures.dreamtrips.wallet.analytics.SetupUserAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAndStoreDefaultAddressInfoCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.Action;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.setup.WizardPinSetupPath;
import com.worldventures.dreamtrips.wallet.util.FirstNameException;
import com.worldventures.dreamtrips.wallet.util.LastNameException;
import com.worldventures.dreamtrips.wallet.util.MiddleNameException;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;
   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SessionHolder<UserSession> appSessionHolder;

   @Nullable private SmartCardUserPhoto preparedPhoto;

   public WizardEditProfilePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      analyticsInteractor.walletAnalyticsCommandPipe().send(new WalletAnalyticsCommand(new SetupUserAction()));
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      // TODO: 9/1/16 investigate and fix this problem (photo piker brake SoftInputMode too)
      // hotfix, web view brake SoftInputMode (set ADJUST_RESIZE)
      setupInputMode();

      observePickerAndCropper(view);
      subscribePreparingAvatarCommand();
      subscribeSetupUserCommand();
      fetchAndStoreDefaultAddress();

      User userProfile = appSessionHolder.get().get().getUser();
      view.setUserFullName(userProfile.getFirstName(), userProfile.getLastName());
      String defaultUserAvatar = userProfile.getAvatar().getThumb();
      if (!TextUtils.isEmpty(defaultUserAvatar)) {
         smartCardUserDataInteractor.smartCardAvatarPipe().send(new LoadImageForSmartCardCommand(defaultUserAvatar));
      }
   }

   void setupInputMode() {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   private void observePickerAndCropper(Screen view) {
      view.observePickPhoto().compose(bindView()).subscribe(view::cropPhoto);

      view.observeCropper().compose(bindView()).subscribe(this::prepareImage);
   }

   private void subscribePreparingAvatarCommand() {
      smartCardUserDataInteractor.smartCardAvatarPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardAvatarCommand>()
                  .onFail((command, throwable) -> Timber.e("", throwable))
                  .onSuccess(command -> photoPrepared(command.getResult())));
   }

   private void subscribeSetupUserCommand() {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .subscribe(OperationActionStateSubscriberWrapper.<SetupUserDataCommand>forView(getView().provideOperationDelegate())
                  .onStart(getContext().getString(R.string.wallet_long_operation_hint))
                  .onSuccess(setupUserDataCommand -> onUserSetupSuccess(setupUserDataCommand.getResult()))
                  .onFail(ErrorHandler.<SetupUserDataCommand>builder(getContext())
                        .handle(FirstNameException.class, R.string.wallet_edit_profile_first_name_format_detail)
                        .handle(LastNameException.class, R.string.wallet_edit_profile_last_name_format_detail)
                        .handle(MiddleNameException.class, R.string.wallet_edit_profile_middle_name_format_detail)
                        .handle(SetupUserDataCommand.MissedAvatarException.class, R.string.wallet_edit_profile_avatar_not_chosen)
                        .build())
                  .wrap());
   }

   private void onUserSetupSuccess(SmartCardUser user) {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new PhotoWasSetAction()));
      navigator.go(new WizardPinSetupPath(Action.SETUP));
   }

   private void photoPrepared(SmartCardUserPhoto photo) {
      preparedPhoto = photo;
      getView().setPreviewPhoto(photo.monochrome());
   }

   void goToBack() {
      getView().hidePhotoPicker();
      navigator.goBack();
   }

   void choosePhoto() {
      getView().pickPhoto();
   }

   private void prepareImage(String path) {
      smartCardUserDataInteractor.smartCardAvatarPipe().send(new CompressImageForSmartCardCommand(path));
   }

   void setupUserData() {
      final String[] userNames = getView().getUserName();
      wizardInteractor.setupUserDataPipe()
            .send(new SetupUserDataCommand(userNames[0], userNames[1], userNames[2], preparedPhoto));
   }

   private void fetchAndStoreDefaultAddress() {
      wizardInteractor
            .fetchAndStoreDefaultAddressInfoPipe().send(new FetchAndStoreDefaultAddressInfoCommand());
   }

   public interface Screen extends WalletScreen {
      void pickPhoto();

      void cropPhoto(String photoPath);

      Observable<String> observePickPhoto();

      Observable<String> observeCropper();

      void hidePhotoPicker();

      void setPreviewPhoto(File photo);

      void setUserFullName(String firstName, String lastName);

      @NonNull
      String[] getUserName();
   }
}
