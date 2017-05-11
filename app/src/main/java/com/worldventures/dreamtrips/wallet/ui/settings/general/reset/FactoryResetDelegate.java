package com.worldventures.dreamtrips.wallet.ui.settings.general.reset;


import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.EnterPinUnAssignAction;
import com.worldventures.dreamtrips.wallet.analytics.new_smartcard.EnterPinUnAssignEnteredAction;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetOptions;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.pin.EnterPinUnassignPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.newcard.success.UnassignSuccessPath;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.success.FactoryResetSuccessPath;

import flow.path.Path;
import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.smartcard.action.lock.LockDeviceAction;
import rx.android.schedulers.AndroidSchedulers;

public abstract class FactoryResetDelegate {
   private final FactoryResetInteractor factoryResetInteractor;
   private final AnalyticsInteractor analyticsInteractor;
   private final PinMode pinMode;
   protected final Navigator navigator;

   private FactoryResetDelegate(FactoryResetInteractor factoryResetInteractor,
         AnalyticsInteractor analyticsInteractor, Navigator navigator, PinMode pinMode) {
      this.factoryResetInteractor = factoryResetInteractor;
      this.analyticsInteractor = analyticsInteractor;
      this.navigator = navigator;
      this.pinMode = pinMode;
   }

   public static FactoryResetDelegate create(FactoryResetInteractor factoryResetInteractor,
         AnalyticsInteractor analyticsInteractor, Navigator navigator, FactoryResetAction action) {
      return FactoryResetDelegate.create(factoryResetInteractor, analyticsInteractor, navigator, action, PinMode.ENABLED);
   }

   public static FactoryResetDelegate create(FactoryResetInteractor factoryResetInteractor,
         AnalyticsInteractor analyticsInteractor, Navigator navigator, FactoryResetAction action, PinMode pinMode) {
      if (action == FactoryResetAction.GENERAL) {
         return new GeneralFactoryResetDelegate(factoryResetInteractor, analyticsInteractor, navigator, pinMode);
      } else {
         return new NewCardFactoryResetDelegate(factoryResetInteractor, analyticsInteractor, navigator, pinMode);
      }
   }

   protected final void trackScreen() {
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(trackScreenAnalyticsAction()));
   }

   protected void observeResetSmartCard(FactoryResetView view) {
      factoryResetInteractor.resetSmartCardCommandActionPipe()
            .observe()
            .compose(RxLifecycle.bindView(view.getView()))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(view.provideResetOperationView(this))
                  .onSuccess(command -> handleSuccessResult())
                  .create());

      factoryReset();
   }

   protected void handleSuccessResult() {
      navigator.single(provideSuccessStep());
   }

   public void setupDelegate(FactoryResetView view) {
      if (pinMode == PinMode.ENABLED) {
         navigator.go(provideStartStep());
      } else {
         bindView(view);
      }
   }

   public void factoryReset() {
      factoryResetInteractor.factoryResetCommandActionPipe().send(new FactoryResetCommand(provideResetOptions()));
   }

   public void cancelFactoryReset() {
      factoryResetInteractor.factoryResetCommandActionPipe().cancelLatest();
      factoryResetInteractor.lockDevicePipe().send(new LockDeviceAction(false));
   }

   public void goBack() {
      navigator.goBack();
   }

   public abstract void bindView(FactoryResetView view);

   protected abstract WalletAnalyticsAction trackScreenAnalyticsAction();

   protected abstract WalletAnalyticsAction trackResetSuccess();

   protected abstract ResetOptions provideResetOptions();

   protected abstract Path provideStartStep();

   protected abstract Path provideSuccessStep();

   private static class GeneralFactoryResetDelegate extends FactoryResetDelegate {

      private GeneralFactoryResetDelegate(FactoryResetInteractor factoryResetInteractor,
            AnalyticsInteractor analyticsInteractor, Navigator navigator, PinMode pinMode) {
         super(factoryResetInteractor, analyticsInteractor, navigator, pinMode);
      }

      @Override
      public void bindView(FactoryResetView view) {
         observeResetSmartCard(view);
      }

      @Override
      protected WalletAnalyticsAction trackScreenAnalyticsAction() {
         throw new UnsupportedOperationException("Factory reset from settings>general doesn't require analytic");
      }

      @Override
      protected WalletAnalyticsAction trackResetSuccess() {
         throw new UnsupportedOperationException("Factory reset from settings>general doesn't require analytic");
      }

      @Override
      protected ResetOptions provideResetOptions() {
         return ResetOptions.builder()
                     .withEnterPin(true)
                     .build();
      }

      @Override
      protected Path provideStartStep() {
         return new FactoryResetPath();
      }

      @Override
      protected Path provideSuccessStep() {
         return new FactoryResetSuccessPath();
      }
   }

   private static class NewCardFactoryResetDelegate extends FactoryResetDelegate {

      private NewCardFactoryResetDelegate(FactoryResetInteractor factoryResetInteractor,
            AnalyticsInteractor analyticsInteractor, Navigator navigator, PinMode pinMode) {
         super(factoryResetInteractor, analyticsInteractor, navigator, pinMode);
      }

      @Override
      public void bindView(FactoryResetView view) {
         trackScreen();
         observeResetSmartCard(view);
      }

      @Override
      protected WalletAnalyticsAction trackScreenAnalyticsAction() {
         return new EnterPinUnAssignAction();
      }

      @Override
      protected WalletAnalyticsAction trackResetSuccess() {
         return new EnterPinUnAssignEnteredAction();
      }

      @Override
      protected ResetOptions provideResetOptions() {
         return ResetOptions.builder()
               .withEnterPin(true)
               .wipePaymentCards(false)
               .wipeUserSmartCardData(false)
               .build();
      }

      @Override
      protected Path provideStartStep() {
         return new EnterPinUnassignPath();
      }

      @Override
      protected Path provideSuccessStep() {
         return new UnassignSuccessPath();
      }

      @Override
      protected void handleSuccessResult() {
         trackResetSuccess();
         super.handleSuccessResult();
      }
   }
}
