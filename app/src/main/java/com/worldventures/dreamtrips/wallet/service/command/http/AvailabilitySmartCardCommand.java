package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.availability_card.AvailabilitySmartCardHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class AvailabilitySmartCardCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet apiJanet;

   private final String smartCardId;

   public AvailabilitySmartCardCommand(String smartCardId) {
      this.smartCardId = smartCardId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      apiJanet.createPipe(AvailabilitySmartCardHttpAction.class)
            .createObservableResult(new AvailabilitySmartCardHttpAction(String.valueOf(Long.parseLong(smartCardId))))
            .onErrorResumeNext(Observable.just(null))
            .subscribe(action -> callback.onSuccess(null), throwable -> callback.onFail(null));
   }
}
