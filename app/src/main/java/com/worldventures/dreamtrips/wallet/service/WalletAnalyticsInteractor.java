package com.worldventures.dreamtrips.wallet.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.oncard.SendOnCardAnalyticsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class WalletAnalyticsInteractor {

   private final ActionPipe<BaseAnalyticsAction> analyticsActionPipe;
   private final ActionPipe<WalletAnalyticsCommand> walletAnalyticsPipe;
   private final ActionPipe<PaycardAnalyticsCommand> paycardAnalyticsPipe;
   private final ActionPipe<LocateCardAnalyticsCommand> locateCardAnalyticsPipe;
   private final ActionPipe<WalletFirmwareAnalyticsCommand> walletFirmwareAnalyticsPipe;
   private final ActionPipe<SendOnCardAnalyticsCommand> onCardAnalyticsPipe;

   public WalletAnalyticsInteractor(SessionActionPipeCreator walletPipeCreator) {
      analyticsActionPipe = walletPipeCreator.createPipe(BaseAnalyticsAction.class, Schedulers.io());
      walletAnalyticsPipe = walletPipeCreator.createPipe(WalletAnalyticsCommand.class, Schedulers.io());
      paycardAnalyticsPipe = walletPipeCreator.createPipe(PaycardAnalyticsCommand.class, Schedulers.io());
      locateCardAnalyticsPipe = walletPipeCreator.createPipe(LocateCardAnalyticsCommand.class, Schedulers.io());
      walletFirmwareAnalyticsPipe = walletPipeCreator.createPipe(WalletFirmwareAnalyticsCommand.class, Schedulers.io());
      onCardAnalyticsPipe = walletPipeCreator.createPipe(SendOnCardAnalyticsCommand.class, Schedulers.io());
   }

   public WriteActionPipe<BaseAnalyticsAction> analyticsActionPipe() {
      return analyticsActionPipe;
   }

   public WriteActionPipe<WalletAnalyticsCommand> walletAnalyticsPipe() {
      return walletAnalyticsPipe;
   }

   public WriteActionPipe<PaycardAnalyticsCommand> paycardAnalyticsPipe() {
      return paycardAnalyticsPipe;
   }

   public WriteActionPipe<LocateCardAnalyticsCommand> locateCardAnalyticsPipe() {
      return locateCardAnalyticsPipe;
   }

   public WriteActionPipe<WalletFirmwareAnalyticsCommand> walletFirmwareAnalyticsPipe() {
      return walletFirmwareAnalyticsPipe;
   }

   public WriteActionPipe<SendOnCardAnalyticsCommand> onCardAnalyticsPipe() {
      return onCardAnalyticsPipe;
   }
}
