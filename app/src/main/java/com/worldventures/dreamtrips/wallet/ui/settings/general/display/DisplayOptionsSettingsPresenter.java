package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.SaveHomeDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePath;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.GetHomeDisplayTypeAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import rx.Observable;
import timber.log.Timber;

import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.HomeDisplayType;

public class DisplayOptionsSettingsPresenter extends WalletPresenter<DisplayOptionsSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletSettingsInteractor settingsInteractor;

   DisplayOptionsSettingsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeHomeDisplay();
   }

   private void observeHomeDisplay() {
      Observable.combineLatest(
            smartCardInteractor.smartCardUserPipe()
                  .createObservableResult(SmartCardUserCommand.fetch())
                  .map(Command::getResult),
            smartCardInteractor.getHomeDisplayTypePipe().observeSuccess()
                  .map(action -> action.type),
            Pair::new)
            .compose(bindViewIoToMainComposer())
            .take(1)
            .doOnSubscribe(this::fetchDisplayType)
            .subscribe(pair -> getView().setupViewPager(pair.first, pair.second), t -> Timber.e(t, ""));

      smartCardInteractor.getHomeDisplayTypePipe().observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().<GetHomeDisplayTypeAction>provideGetDisplayTypeOperationView())
                  .create()
            );
      settingsInteractor.saveHomeDisplayTypePipe().observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().<SetHomeDisplayTypeAction>provideSaveDisplayTypeOperationView())
                  .create()
            );
   }

   void fetchDisplayType() {smartCardInteractor.getHomeDisplayTypePipe().send(new GetHomeDisplayTypeAction());}

   void saveDisplayType(@HomeDisplayType int type) {
      settingsInteractor.saveHomeDisplayTypePipe().send(new SaveHomeDisplayTypeCommand(type));
   }

   void openEditProfileScreen() {
      navigator.go(new WalletSettingsProfilePath());
   }

   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {

      void setupViewPager(@NonNull SmartCardUser user, @HomeDisplayType int type);

      OperationView<GetHomeDisplayTypeAction> provideGetDisplayTypeOperationView();

      OperationView<SaveHomeDisplayTypeCommand> provideSaveDisplayTypeOperationView();
   }
}