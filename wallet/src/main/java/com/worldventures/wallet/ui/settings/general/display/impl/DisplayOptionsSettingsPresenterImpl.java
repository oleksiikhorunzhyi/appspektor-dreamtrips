package com.worldventures.wallet.ui.settings.general.display.impl;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.worldventures.core.modules.picker.model.PhotoPickerModel;
import com.worldventures.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletSocialInfoProvider;
import com.worldventures.wallet.service.command.SmartCardUserCommand;
import com.worldventures.wallet.service.command.profile.ChangedFields;
import com.worldventures.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter;
import com.worldventures.wallet.ui.settings.general.display.DisplayOptionsSettingsScreen;
import com.worldventures.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.wallet.util.GuaranteedProgressVisibilityTransformer;
import com.worldventures.wallet.util.WalletFilesUtils;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class DisplayOptionsSettingsPresenterImpl extends WalletPresenterImpl<DisplayOptionsSettingsScreen> implements DisplayOptionsSettingsPresenter {

   private final SmartCardInteractor smartCardInteractor;
   private final SmartCardUserDataInteractor smartCardUserDataInteractor;
   private final WalletSocialInfoProvider socialInfoProvider;
   private final WalletProfileDelegate delegate;

   private boolean mustSaveUserProfile;
   private SmartCardUser user;

   public DisplayOptionsSettingsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         SmartCardInteractor smartCardInteractor, SmartCardUserDataInteractor smartCardUserDataInteractor,
         WalletAnalyticsInteractor analyticsInteractor, WalletSocialInfoProvider socialInfoProvider) {
      super(navigator, deviceConnectionDelegate);
      this.smartCardInteractor = smartCardInteractor;
      this.smartCardUserDataInteractor = smartCardUserDataInteractor;
      this.socialInfoProvider = socialInfoProvider;
      this.delegate = new WalletProfileDelegate(smartCardUserDataInteractor, analyticsInteractor);
   }

   @Override
   public void attachView(DisplayOptionsSettingsScreen view) {
      super.attachView(view);
      initiateData();
      observeHomeDisplay();
      observeUserProfileUploading();
      fetchDisplayType();
   }

   private void initiateData() {
      final ProfileViewModel profileViewModel = getView().getProfileViewModel();
      this.user = (profileViewModel != null) ? delegate.createSmartCardUser(getView().getProfileViewModel()) : null;
      this.mustSaveUserProfile = user != null;
   }

   @SuppressWarnings("ConstantConditions")
   private void observeHomeDisplay() {
      Observable.combineLatest(
            getUserObservable(),
            smartCardInteractor.getDisplayTypePipe().observeSuccess()
                  .map(Command::getResult),
            Pair::new)
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .take(1)
            .doOnSubscribe(this::fetchDisplayType)
            .subscribe(pair -> getView().setupViewPager(user, pair.second), t -> Timber.e(t, ""));

      final OperationView<GetDisplayTypeCommand> getDisplayTypeOperationView
            = getView().<GetDisplayTypeCommand>provideGetDisplayTypeOperationView();
      getDisplayTypeOperationView.showProgress(null);
      smartCardInteractor.getDisplayTypePipe().observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getDisplayTypeOperationView)
                  .create()
            );
      smartCardInteractor.saveDisplayTypePipe().observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().<SetHomeDisplayTypeAction>provideSaveDisplayTypeOperationView())
                  .onSuccess(command -> {
                     if (getView().getDisplayOptionsSource().isSettings()) {
                        goBack();
                     } else {
                        getNavigator().goBackToProfile();
                     }
                  })
                  .create()
            );
   }

   private void observeUserProfileUploading() {
      delegate.observeProfileUploading(getView());
   }

   @NonNull
   private Observable<SmartCardUser> getUserObservable() {
      return user != null ? Observable.just(user) : smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .map(Command::getResult).doOnNext(smartCardUser -> user = smartCardUser);
   }

   @Override
   public void fetchDisplayType() {
      smartCardInteractor.getDisplayTypePipe().send(new GetDisplayTypeCommand(true));
   }

   @Override
   public void savePhoneNumber(ProfileViewModel profile) {
      final SmartCardUserPhone enteredPhone = delegate.createPhone(profile);
      if (enteredPhone == null) {
         return;
      }

      user = ImmutableSmartCardUser.builder()
            .from(user)
            .phoneNumber(enteredPhone)
            .build();

      mustSaveUserProfile = true;
      getView().updateUser(user);
   }

   @Override
   public void handlePickedPhoto(PhotoPickerModel model) {
      getView().cropPhoto(WalletFilesUtils.convertPickedPhotoToUri(model));
   }

   @Override
   public void saveAvatar(String imageUri) {
      user = ImmutableSmartCardUser.builder()
            .from(user)
            .userPhoto(new SmartCardUserPhoto(imageUri))
            .build();

      mustSaveUserProfile = true;
      getView().updateUser(user);
   }

   @Override
   public void choosePhoto() {
      getView().pickPhoto(delegate.provideInitialPhotoUrl(socialInfoProvider.photoThumb()));
   }

   @Override
   public void saveDisplayType(@SetHomeDisplayTypeAction.HomeDisplayType int type) {
      final SaveDisplayTypeCommand saveDisplayType = new SaveDisplayTypeCommand(type, user);
      if (mustSaveUserProfile) {
         updateProfileAndSaveDisplayType(saveDisplayType);
      } else {
         smartCardInteractor.saveDisplayTypePipe().send(saveDisplayType);
      }
   }

   private void updateProfileAndSaveDisplayType(SaveDisplayTypeCommand saveDisplayType) {
      final ChangedFields changedFields = ImmutableChangedFields.builder()
            .firstName(user.firstName())
            .middleName(user.middleName())
            .lastName(user.lastName())
            .phone(user.phoneNumber())
            .photo(user.userPhoto())
            .build();

      smartCardUserDataInteractor.updateSmartCardUserPipe()
            .createObservableResult(new UpdateSmartCardUserCommand(changedFields, true))
            .doOnNext(command -> smartCardInteractor.saveDisplayTypePipe().send(saveDisplayType))
            .subscribe(command -> {
            }, throwable -> Timber.e(throwable, ""));
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }
}
