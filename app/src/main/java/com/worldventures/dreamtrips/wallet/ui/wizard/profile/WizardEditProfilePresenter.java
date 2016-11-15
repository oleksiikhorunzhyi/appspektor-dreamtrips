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
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SmartCardAvatarInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.DisassociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAndStoreDefaultAddressInfoCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ErrorHandler;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.exception.SmartCardServiceException;
import rx.Observable;
import timber.log.Timber;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardAvatarInteractor smartCardAvatarInteractor;
   @Inject WizardInteractor wizardInteractor;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SessionHolder<UserSession> appSessionHolder;

   @Nullable private SmartCardUserPhoto preparedPhoto;

   private final String smartCardId;

   public WizardEditProfilePresenter(Context context, Injector injector, String smartCardId) {
      super(context, injector);
      this.smartCardId = smartCardId;
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

      // TODO: 10/13/16 for remove pin setup screen
      observeActivation();

      User userProfile = appSessionHolder.get().get().getUser();
      view.setUserFullName(userProfile.getFullName());
      String defaultUserAvatar = userProfile.getAvatar().getThumb();
      if (!TextUtils.isEmpty(defaultUserAvatar)) {
         smartCardAvatarInteractor.smartCardAvatarPipe().send(new LoadImageForSmartCardCommand(defaultUserAvatar));
      }
   }

   public void setupInputMode() {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   private void observePickerAndCropper(Screen view) {
      view.observePickPhoto().compose(bindView()).subscribe(view::cropPhoto);

      view.observeCropper().compose(bindView()).subscribe(this::prepareImage);
   }

   private void subscribePreparingAvatarCommand() {
      smartCardAvatarInteractor.smartCardAvatarPipe()
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
                        .handle(FormatException.class, R.string.wallet_edit_profile_name_format_detail)
                        .handle(SetupUserDataCommand.MissedAvatarException.class, R.string.wallet_edit_profile_avatar_not_chosen)
                        .handle(SmartCardServiceException.class, command -> smartCardError(smartCardId))
                        .build())
                  .wrap());
   }

   private void observeActivation() {
      wizardInteractor.activateSmartCardPipe()
            .observeWithReplay()
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.activateSmartCardPipe()))
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionStateSubscriberWrapper.<ActivateSmartCardCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(command -> navigateToDashboardScreen())
                  .onFail(getContext().getString(R.string.error_something_went_wrong))
                  .wrap());
   }

   private void navigateToDashboardScreen() {
      navigator.single(new CardListPath());
   }

   private void onUserSetupSuccess(SmartCard smartCard) {
      // TODO: 10/13/16 for remove pis setup screen
      wizardInteractor.activateSmartCardPipe().send(new ActivateSmartCardCommand(smartCard));
      //      navigator.go(new WizardPinSetupPath(smartCard, Action.SETUP));
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new PhotoWasSetAction(smartCard.cardName(), smartCardId)));
   }

   private void smartCardError(String smartCardId) {
      navigator.goBack();
      wizardInteractor.disassociatePipe().send(new DisassociateCardUserCommand(smartCardId));
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
      smartCardAvatarInteractor.smartCardAvatarPipe().send(new CompressImageForSmartCardCommand(path));
   }

   void setupUserData() {
      wizardInteractor.setupUserDataPipe().send(new SetupUserDataCommand(getView().getUserName()
            .trim(), preparedPhoto, smartCardId));
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

      void setUserFullName(String fullName);

      @NonNull
      String getUserName();
   }
}