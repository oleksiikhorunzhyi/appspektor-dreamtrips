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
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.service.SmartCardAvatarInteractor;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPath;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import java.io.File;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import timber.log.Timber;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

   @Inject Activity activity;
   @Inject Navigator navigator;
   @Inject SmartCardAvatarInteractor smartCardAvatarInteractor;
   @Inject WizardInteractor wizardInteractor;
   @Inject SessionHolder<UserSession> appSessionHolder;

   @Nullable private File preparedPhotoFile;

   private final String smartCardId;

   public WizardEditProfilePresenter(Context context, Injector injector, String smartCardId) {
      super(context, injector);
      this.smartCardId = smartCardId;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      // TODO: 9/1/16 investigate and fix this problem (photo piker brake SoftInputMode too)
      // hotfix, web view brake SoftInputMode (set ADJUST_RESIZE)
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

      observePickerAndCropper(view);
      subscribePreparingAvatarCommand();
      subscribeSetupUserCommand();

      User userProfile = appSessionHolder.get().get().getUser();
      view.setUserFullName(userProfile.getFullName());
      String defaultUserAvatar = userProfile.getAvatar().getThumb();
      if (!TextUtils.isEmpty(defaultUserAvatar)) {
         smartCardAvatarInteractor.smartCardAvatarPipe().send(new LoadImageForSmartCardCommand(defaultUserAvatar));
      }
   }


   private void observePickerAndCropper(Screen view) {
      view.observePickPhoto().compose(bindView()).subscribe(view::cropPhoto);

      view.observeCropper().compose(bindView()).subscribe(this::prepareImage);
   }

   public void subscribePreparingAvatarCommand() {
      smartCardAvatarInteractor.smartCardAvatarPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(new ActionStateSubscriber<SmartCardAvatarCommand>().onFail((command, throwable) -> Timber.e("", throwable))
                  .onSuccess(command -> photoPrepared(command.getResult())));
   }

   public void subscribeSetupUserCommand() {
      wizardInteractor.setupUserDataPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.setupUserDataPipe()))
            .subscribe(OperationSubscriberWrapper.<SetupUserDataCommand>forView(getView().provideOperationDelegate())
                  .onSuccess(setupUserDataCommand -> navigator.go(new WizardPinSetupPath(setupUserDataCommand.getResult())))
                  .onFail(throwable -> createFailMessageActionHolder(throwable.getCause()))
                  .wrap());
   }

   private OperationSubscriberWrapper.MessageActionHolder createFailMessageActionHolder(Throwable throwable) {
      Context context = getContext();
      String msg;
      if (throwable instanceof FormatException) {
         msg = context.getString(R.string.wallet_edit_profile_name_format_detail);
      } else if (throwable instanceof SetupUserDataCommand.MissedAvatarException) {
         msg = context.getString(R.string.wallet_edit_profile_avatar_not_chosen);
      } else {
         msg = context.getString(R.string.error_something_went_wrong);
      }
      return new OperationSubscriberWrapper.MessageActionHolder<>(msg, null);
   }

   private void photoPrepared(File filePhoto) {
      preparedPhotoFile = filePhoto;
      getView().setPreviewPhoto(filePhoto);
   }

   public void goToBack() {
      getView().hidePhotoPicker();
      navigator.goBack();
   }

   public void choosePhoto() {
      getView().pickPhoto();
   }

   private void prepareImage(String path) {
      smartCardAvatarInteractor.smartCardAvatarPipe().send(new CompressImageForSmartCardCommand(path));
   }

   public void setupUserData() {
      wizardInteractor.setupUserDataPipe().send(new SetupUserDataCommand(getView().getUserName()
            .trim(), preparedPhotoFile, smartCardId));
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
