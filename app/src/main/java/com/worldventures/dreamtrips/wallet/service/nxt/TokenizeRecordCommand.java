package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecord;
import com.worldventures.dreamtrips.wallet.service.nxt.util.TokenizedRecord;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.JanetNxtModule.JANET_NXT;

@CommandAction
public class TokenizeRecordCommand extends Command<NxtRecord> implements InjectableAction {

   @Inject @Named(JANET_NXT) Janet janet;

   private final Record card;

   public TokenizeRecordCommand(Record card) {
      this.card = card;
   }

   @Override
   protected void run(CommandCallback<NxtRecord> callback) throws Exception {
      janet.createPipe(MultifunctionNxtHttpAction.class, Schedulers.io())
            .createObservableResult(new MultifunctionNxtHttpAction(createRequestBody()))
            .map(actionResponse -> createResponseBody(actionResponse.getResponse()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private MultiRequestBody createRequestBody() {
      return ImmutableMultiRequestBody.builder()
            .multiRequestElements(NxtBankCardHelper.getDataForTokenization(card))
            .build();
   }

   private NxtRecord createResponseBody(MultiResponseBody response) {
      return TokenizedRecord.from(card, response);
   }

}