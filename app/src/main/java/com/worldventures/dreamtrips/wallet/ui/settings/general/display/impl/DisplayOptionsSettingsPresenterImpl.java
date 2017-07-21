package com.worldventures.dreamtrips.wallet.ui.settings.general.display.impl;


import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.NavigatorConductor;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSettingsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.general.display.DisplayOptionsSource;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.Observable;
import timber.log.Timber;

public class DisplayOptionsSettingsPresenterImpl extends WalletPresenterImpl<DisplayOptionsSettingsScreen> implements DisplayOptionsSettingsPresenter {

   private DisplayOptionsSource source;
   private SmartCardUser user;

   public DisplayOptionsSettingsPresenterImpl(NavigatorConductor navigator, SmartCardInteractor smartCardInteractor,
         WalletNetworkService networkService) {
      super(navigator, smartCardInteractor, networkService);
   }

   @Override
   public void attachView(DisplayOptionsSettingsScreen view) {
      super.attachView(view);
      this.source = getView().getDisplayOptionsSource();
      this.user = getView().getSmartCardUser();
      observeHomeDisplay();
      fetchDisplayType();
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
                  .onSuccess(command -> goBack())
                  .create()
            );
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
      getSmartCardInteractor().saveDisplayTypePipe().send(new SaveDisplayTypeCommand(type, user));
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
}
