package com.worldventures.dreamtrips.wallet.ui.settings.general.display.impl;


import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.ProfileViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.Observable;
import timber.log.Timber;

public class DisplayOptionsSettingsPresenterImpl extends WalletPresenterImpl<DisplayOptionsSettingsScreen> implements DisplayOptionsSettingsPresenter {

   private final SmartCardUserDataInteractor smartCardUserDataInteractor;

   private WalletProfileDelegate delegate;
   private boolean mustSaveUserProfile;
   private DisplayOptionsSource source;
   private SmartCardUser user;

   public DisplayOptionsSettingsPresenterImpl(Navigator navigator, SmartCardInteractor smartCardInteractor,
         SmartCardUserDataInteractor smartCardUserDataInteractor, WalletNetworkService networkService,
         AnalyticsInteractor analyticsInteractor) {
      super(navigator, smartCardInteractor, networkService);
      this.smartCardUserDataInteractor = smartCardUserDataInteractor;
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
      this.source = getView().getDisplayOptionsSource();
      final ProfileViewModel profileViewModel = getView().getProfileViewModel();
      this.user = (profileViewModel != null) ? delegate.createSmartCardUser(getView().getProfileViewModel()) : null;
      this.mustSaveUserProfile = user != null;
   }

   @SuppressWarnings("ConstantConditions")
   private void observeHomeDisplay() {
      Observable.combineLatest(
            getUserObservable(),
            getSmartCardInteractor().getDisplayTypePipe().observeSuccess()
                  .map(Command::getResult),
            Pair::new)
            .compose(bindViewIoToMainComposer())
            .take(1)
            .doOnSubscribe(this::fetchDisplayType)
            .subscribe(pair -> getView().setupViewPager(pair.first, pair.second), t -> Timber.e(t, ""));

      final OperationView<GetDisplayTypeCommand> getDisplayTypeOperationView =
            getView().<GetDisplayTypeCommand>provideGetDisplayTypeOperationView();
      getDisplayTypeOperationView.showProgress(null);
      getSmartCardInteractor().getDisplayTypePipe().observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getDisplayTypeOperationView)
                  .create()
            );
      getSmartCardInteractor().saveDisplayTypePipe().observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().<SetHomeDisplayTypeAction>provideSaveDisplayTypeOperationView())
                  .onSuccess(command -> {
                     if (mustSaveUserProfile) returnToDashboard();
                     else goBack();
                  })
                  .create()
            );
   }

   private void observeUserProfileUploading() {
      delegate.observeProfileUploading(getView());
   }

   @NonNull
   private Observable<SmartCardUser> getUserObservable() {
      return (user != null) ? Observable.just(user) : getSmartCardInteractor().smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .map(Command::getResult).doOnNext(smartCardUser -> user = smartCardUser);
   }

   @Override
   public void fetchDisplayType() {getSmartCardInteractor().getDisplayTypePipe().send(new GetDisplayTypeCommand(true));}

   @Override
   public void saveDisplayType(@SetHomeDisplayTypeAction.HomeDisplayType int type) {
      final SaveDisplayTypeCommand saveDisplayType = new SaveDisplayTypeCommand(type, user);
      if (mustSaveUserProfile) {
         updateProfileAndSaveDisplayType(saveDisplayType);
      } else {
         getSmartCardInteractor().saveDisplayTypePipe().send(saveDisplayType);
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
            .doOnNext(command -> getSmartCardInteractor().saveDisplayTypePipe().send(saveDisplayType))
            .subscribe(command -> {
            }, throwable -> Timber.e(throwable, ""));
   }

   @Override
   public void openEditProfileScreen() {
      if (source.isSettings()) {
         getNavigator().goSettingsProfile();
      } else {
         goBack();
      }
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   private void returnToDashboard() {
      getNavigator().goCardList();
   }

}
