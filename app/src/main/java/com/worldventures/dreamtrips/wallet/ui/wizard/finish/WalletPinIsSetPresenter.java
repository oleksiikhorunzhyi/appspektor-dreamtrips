package com.worldventures.dreamtrips.wallet.ui.wizard.finish;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.PinWasSetAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationActionStateSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;

import javax.inject.Inject;

public class WalletPinIsSetPresenter extends WalletPresenter<WalletPinIsSetPresenter.Screen, Parcelable> {

   @Inject WizardInteractor wizardInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject Navigator navigator;

   private final SmartCard smartCard;

   public WalletPinIsSetPresenter(Context context, Injector injector, SmartCard smartCard) {
      super(context, injector);
      this.smartCard = smartCard;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      analyticsInteractor.walletAnalyticsCommandPipe()
            .send(new WalletAnalyticsCommand(new PinWasSetAction(smartCard.cardName())));
      observeActivation();
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

   public void goBack() {
      navigator.goBack();
   }

   public void activateSmartCard() {
      wizardInteractor.activateSmartCardPipe().send(new ActivateSmartCardCommand(smartCard));
   }

   private void navigateToDashboardScreen() {
      navigator.single(new CardListPath());
   }

   public interface Screen extends WalletScreen {

   }
}
