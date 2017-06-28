package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePath;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;

import javax.inject.Inject;

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

   private final DisplayOptionsSource source;
   private SmartCardUser user;

   DisplayOptionsSettingsPresenter(Context context, Injector injector, DisplayOptionsSource displayOptionsSource,
         @Nullable SmartCardUser smartCardUser) {
      super(context, injector);
      this.source = displayOptionsSource;
      this.user = smartCardUser;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeHomeDisplay();
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
                  .onSuccess(command -> goBack())
                  .create()
            );
   }

   @NonNull
   private Observable<SmartCardUser> getUserObservable() {
      return (user != null) ? Observable.just(user) : smartCardInteractor.smartCardUserPipe()
            .createObservableResult(SmartCardUserCommand.fetch())
            .map(Command::getResult).doOnNext(smartCardUser -> user = smartCardUser);
   }

   void fetchDisplayType() {smartCardInteractor.getDisplayTypePipe().send(new GetDisplayTypeCommand(true));}

   void saveDisplayType(@HomeDisplayType int type) {
      smartCardInteractor.saveDisplayTypePipe().send(new SaveDisplayTypeCommand(type, user));
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

   public interface Screen extends WalletScreen {

      void setupViewPager(@NonNull SmartCardUser user, @HomeDisplayType int type);

      OperationView<GetDisplayTypeCommand> provideGetDisplayTypeOperationView();

      OperationView<SaveDisplayTypeCommand> provideSaveDisplayTypeOperationView();
   }
}