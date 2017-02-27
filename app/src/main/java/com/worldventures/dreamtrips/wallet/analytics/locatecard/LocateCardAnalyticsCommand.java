package com.worldventures.dreamtrips.wallet.analytics.locatecard;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.lostcard.command.DetectGeoLocationCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class LocateCardAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject AnalyticsInteractor analyticsInteractor;

   private final BaseLocateSmartCardAction baseLocateSmartCardAction;

   public LocateCardAnalyticsCommand(BaseLocateSmartCardAction baseLocateSmartCardAction) {
      this.baseLocateSmartCardAction = baseLocateSmartCardAction;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(DetectGeoLocationCommand.class)
            .createObservableResult(new DetectGeoLocationCommand())
            .flatMap(command -> {
               baseLocateSmartCardAction.setLocation(command.getResult().lat(), command.getResult().lng());
               return analyticsInteractor.walletAnalyticsCommandPipe()
                     .createObservableResult(new WalletAnalyticsCommand(baseLocateSmartCardAction));
            })
            .map(Command::getResult)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
