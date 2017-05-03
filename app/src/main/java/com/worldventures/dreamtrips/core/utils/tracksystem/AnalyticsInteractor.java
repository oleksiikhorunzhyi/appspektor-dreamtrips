package com.worldventures.dreamtrips.core.utils.tracksystem;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class AnalyticsInteractor {

   private final ActionPipe<BaseAnalyticsAction> analyticsActionPipe;
   private final ActionPipe<DtlAnalyticsCommand> analyticsCommandPipe;
   private final ActionPipe<WalletAnalyticsCommand> walletAnalyticsCommandPipe;
   private final ActionPipe<PaycardAnalyticsCommand> paycardAnalyticsCommandPipe;

   public AnalyticsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      analyticsActionPipe = sessionActionPipeCreator.createPipe(BaseAnalyticsAction.class, Schedulers.io());
      analyticsCommandPipe = sessionActionPipeCreator.createPipe(DtlAnalyticsCommand.class, Schedulers.io());
      walletAnalyticsCommandPipe = sessionActionPipeCreator.createPipe(WalletAnalyticsCommand.class, Schedulers.io());
      paycardAnalyticsCommandPipe = sessionActionPipeCreator.createPipe(PaycardAnalyticsCommand.class, Schedulers.io());
   }

   public WriteActionPipe<BaseAnalyticsAction> analyticsActionPipe() {
      return analyticsActionPipe;
   }

   public WriteActionPipe<DtlAnalyticsCommand> dtlAnalyticsCommandPipe() {
      return analyticsCommandPipe;
   }

   public WriteActionPipe<WalletAnalyticsCommand> walletAnalyticsCommandPipe() {
      return walletAnalyticsCommandPipe;
   }

   public WriteActionPipe<PaycardAnalyticsCommand> paycardAnalyticsCommandPipe() {
      return paycardAnalyticsCommandPipe;
   }
}
