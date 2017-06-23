package com.worldventures.dreamtrips.wallet.ui.settings.general.display;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.general.display.SaveHomeDisplayTypeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.profile.WalletSettingsProfilePath;
import com.worldventures.dreamtrips.wallet.util.GuaranteedProgressVisibilityTransformer;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;
import io.techery.janet.smartcard.action.settings.GetHomeDisplayTypeAction;
import io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction;
import timber.log.Timber;

import static io.techery.janet.smartcard.action.settings.SetHomeDisplayTypeAction.HomeDisplayType;

public class DisplayOptionsSettingsPresenter extends WalletPresenter<DisplayOptionsSettingsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject WalletSettingsInteractor settingsInteractor;

   private final SmartCardUser user;
   private final DisplayOptionsSource source;

   DisplayOptionsSettingsPresenter(Context context, Injector injector, SmartCardUser smartCardUser,
         DisplayOptionsSource displayOptionsSource) {
      super(context, injector);
      this.user = smartCardUser;
      this.source = displayOptionsSource;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      observeHomeDisplay();
   }

   @SuppressWarnings("ConstantConditions")
   private void observeHomeDisplay() {
      smartCardInteractor.getHomeDisplayTypePipe().observeSuccess()
            .map(action -> action.type)
            .compose(bindViewIoToMainComposer())
            .take(1)
            .doOnSubscribe(this::fetchDisplayType)
            .subscribe(type -> getView().setupViewPager(user, type), t -> Timber.e(t, ""));

      final OperationView<GetHomeDisplayTypeAction> getHomeDisplayTypeOperationView =
            getView().<GetHomeDisplayTypeAction>provideGetDisplayTypeOperationView();
      getHomeDisplayTypeOperationView.showProgress(null);
      smartCardInteractor.getHomeDisplayTypePipe().observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getHomeDisplayTypeOperationView)
                  .create()
            );
      settingsInteractor.saveHomeDisplayTypePipe().observe()
            .compose(new GuaranteedProgressVisibilityTransformer<>())
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().<SetHomeDisplayTypeAction>provideSaveDisplayTypeOperationView())
                  .onSuccess(command -> goBack())
                  .create()
            );
   }

   void fetchDisplayType() {smartCardInteractor.getHomeDisplayTypePipe().send(new GetHomeDisplayTypeAction());}

   void saveDisplayType(@HomeDisplayType int type) {
      settingsInteractor.saveHomeDisplayTypePipe().send(new SaveHomeDisplayTypeCommand(user, type));
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

      OperationView<GetHomeDisplayTypeAction> provideGetDisplayTypeOperationView();

      OperationView<SaveHomeDisplayTypeCommand> provideSaveDisplayTypeOperationView();
   }
}