package com.worldventures.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.nxt.CreateNxtSessionHttpAction;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.session.NxtSessionHolder;
import com.worldventures.wallet.service.nxt.model.NxtSession;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class CreateNxtSessionCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject NxtSessionHolder nxtSessionHolder;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(CreateNxtSessionHttpAction.class)
            .createObservableResult(new CreateNxtSessionHttpAction())
            .map(action -> mapperyContext.convert(action.response(), NxtSession.class))
            .doOnNext(nxtSession -> nxtSessionHolder.put(nxtSession))
            .map(nxtSession -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
