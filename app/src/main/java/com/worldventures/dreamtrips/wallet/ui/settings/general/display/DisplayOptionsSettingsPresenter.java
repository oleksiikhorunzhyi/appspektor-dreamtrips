package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.ChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.ImmutableChangedFields;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.UpdateSmartCardUserView;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common.WalletProfileDelegate;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.Observable;
import timber.log.Timber;

import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.HomeDisplayType;

public class DisplayOptionsSettingsPresenter extends WalletPresenter<DisplayOptionsSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SmartCardUserDataInteractor smartCardUserDataInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final DisplayOptionsSource source;
   private final WalletProfileDelegate delegate;
   private final boolean mustSaveUserProfile;

   private SmartCardUser user;

   DisplayOptionsSettingsPresenter(Context context, Injector injector,
         DisplayOptionsSource displayOptionsSource, @Nullable SmartCardUser smartCardUser) {
      super(context, injector);
      this.source = displayOptionsSource;
      this.user = smartCardUser;
      this.mustSaveUserProfile = user != null;
      this.delegate = new WalletProfileDelegate(smartCardUserDataInteractor, analyticsInteractor);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeHomeDisplay();
      observeUserProfileUploading();
      fetchDisplayType();
   }

   @SuppressWarnings("ConstantConditions")
   private void observeHomeDisplay() {
      Observable.combineLatest(
            getUserObservable(),
            smartCardInteractor.getDisplayTypePipe().observeSuccess()
                  .map(Command::getResult),
            Pair::new)
            .compose(bindViewIoToMainComposer())
            .take(1)
            .doOnSubscribe(this::fetchDisplayType)
            .subscribe(pair -> getView().setupViewPager(pair.first, pair.second), t -> Timber.e(t, ""));

      final OperationView<GetDisplayTypeCommand> getDisplayTypeOperationView =
            getView().<GetDisplayTypeCommand>provideGetDisplayTypeOperationView();
      getDisplayTypeOperationView.showProgress(null);
      smartCardInteractor.getDisplayTypePipe().observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getDisplayTypeOperationView)
                  .create()
            );
      smartCardInteractor.saveDisplayTypePipe().observe()
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
      return (user != null) ? Observable.just(user) : smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .map(Command::getResult).doOnNext(smartCardUser -> user = smartCardUser);
   }

   void fetchDisplayType() {smartCardInteractor.getDisplayTypePipe().send(new GetDisplayTypeCommand(true));}

   void saveDisplayType(@HomeDisplayType int type) {
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

   void openEditProfileScreen() {
      if (source.isSettings()) {
         navigator.go(new WalletSettingsProfilePath());
      } else {
         goBack();
      }
   }

   public void goBack() {
      navigator.goBack();
   }

   private void returnToDashboard() {navigator.single(new CardListPath(), Flow.Direction.REPLACE);}

   public interface Screen extends WalletScreen, UpdateSmartCardUserView {

      void setupViewPager(@NonNull SmartCardUser user, @HomeDisplayType int type);

      OperationView<GetDisplayTypeCommand> provideGetDisplayTypeOperationView();

      OperationView<SaveDisplayTypeCommand> provideSaveDisplayTypeOperationView();
   }
}