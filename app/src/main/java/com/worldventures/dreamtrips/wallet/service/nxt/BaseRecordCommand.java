package com.worldventures.dreamtrips.wallet.service.nxt;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestElement;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecord;
import com.worldventures.dreamtrips.wallet.util.NxtMultifunctionException;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.JanetNxtModule.JANET_NXT;

public abstract class BaseRecordCommand extends Command<Record> implements InjectableAction {

   @Inject @Named(JANET_NXT) Janet janet;

   private final Record record;
   private final boolean tokenize;

   BaseRecordCommand(Record record, boolean tokenize) {
      this.record = record;
      this.tokenize = tokenize;
   }

   @Override
   protected void run(CommandCallback<Record> callback) throws Exception {
      janet.createPipe(MultifunctionNxtHttpAction.class, Schedulers.io())
            .createObservableResult(new MultifunctionNxtHttpAction(createRequestBody()))
            .map(actionResponse -> createResponseBody(this.record, actionResponse.getResponse()))
            .flatMap(handleRecordResult())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private MultiRequestBody createRequestBody() {
      return ImmutableMultiRequestBody.builder()
            .multiRequestElements(prepareMultiRequestElements(record))
            .build();
   }

   abstract List<MultiRequestElement> prepareMultiRequestElements(Record record);

   abstract NxtRecord createResponseBody(Record record, MultiResponseBody nxtResponse);

   @NonNull
   private Func1<NxtRecord, Observable<Record>> handleRecordResult() {
      return nxtRecord -> {
         if (nxtRecord.getResponseErrors().isEmpty()) {
            return Observable.just(tokenize ? nxtRecord.getTokenizedRecord() : nxtRecord.getDetokenizedRecord());
         } else {
            return Observable.error(new NxtMultifunctionException(
                  NxtBankCardHelper.getResponseErrorMessage(nxtRecord.getResponseErrors())));
         }
      };
   }

}