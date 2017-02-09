package com.worldventures.dreamtrips.wallet.service.nxt;

import com.techery.spares.session.NxtSessionHolder;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;

import static com.worldventures.dreamtrips.wallet.di.JanetNxtModule.JANET_NXT;

public abstract class BaseNxtCommand<T> extends Command<T> implements InjectableAction {

   @Inject @Named(JANET_NXT) Janet janet;
   @Inject NxtSessionHolder nxtSessionHolder;

   protected final BankCard card;

   public BaseNxtCommand(BankCard card) {
      this.card = card;
   }

   protected String getSessionToken() {
      return (nxtSessionHolder.get() != null && nxtSessionHolder.get().isPresent()) ?
            nxtSessionHolder.get().get().token() : null;
   }

}