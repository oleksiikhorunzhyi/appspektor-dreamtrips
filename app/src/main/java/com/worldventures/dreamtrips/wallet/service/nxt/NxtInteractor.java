package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.wallet.service.command.http.CreateNxtSessionCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class NxtInteractor {

   private final ActionPipe<CreateNxtSessionCommand> createNxtSessionPipe;

   private final ActionPipe<TokenizeBankCardCommand> tokenizeBankCardPipe;
   private final ActionPipe<DetokenizeBankCardCommand> detokenizeBankCardPipe;

   public NxtInteractor(Janet janet) {
      createNxtSessionPipe = janet.createPipe(CreateNxtSessionCommand.class, Schedulers.io());

      tokenizeBankCardPipe = janet.createPipe(TokenizeBankCardCommand.class, Schedulers.io());
      detokenizeBankCardPipe = janet.createPipe(DetokenizeBankCardCommand.class, Schedulers.io());
   }

   public ActionPipe<CreateNxtSessionCommand> createNxtSessionPipe() {
      return createNxtSessionPipe;
   }

   public ActionPipe<TokenizeBankCardCommand> tokenizeBankCardPipe() {
      return tokenizeBankCardPipe;
   }

   public ActionPipe<DetokenizeBankCardCommand> detokenizeBankCardPipe() {
      return detokenizeBankCardPipe;
   }

}