package com.worldventures.dreamtrips.wallet.ui.records.connectionerror;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.records.swiping.WizardChargingPath;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;

public class ConnectionErrorPresenter extends WalletPresenter<ConnectionErrorPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject SmartCardInteractor smartCardInteractor;

   public ConnectionErrorPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeConnection();
   }

   private void observeConnection() {
      smartCardInteractor.activeSmartCardPipe()
            .observeSuccessWithReplay()
            .throttleLast(1, TimeUnit.SECONDS)
            .map(Command::getResult)
            .map(SmartCard::connectionStatus)
            .distinctUntilChanged()
            .compose(bindViewIoToMainComposer())
            .subscribe(connectionStatus -> {
               if (connectionStatus.isConnected()) navigator.withoutLast(new WizardChargingPath());
            });

      smartCardInteractor.activeSmartCardPipe().send(new ActiveSmartCardCommand());
   }

   public void onBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
   }
}
