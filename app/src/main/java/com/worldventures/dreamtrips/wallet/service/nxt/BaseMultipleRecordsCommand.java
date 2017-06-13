package com.worldventures.dreamtrips.wallet.service.nxt;

import android.support.annotation.NonNull;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiErrorResponse;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestElement;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecord;
import com.worldventures.dreamtrips.wallet.util.NxtMultifunctionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.JanetNxtModule.JANET_NXT;
import static com.worldventures.dreamtrips.wallet.service.nxt.MultifunctionNxtHttpAction.MULTIFUNCTION_REQUEST_ELEMENTS_LIMIT;

public abstract class BaseMultipleRecordsCommand extends Command<List<Record>> implements InjectableAction {

   @Inject @Named(JANET_NXT) Janet janet;

   private final List<Record> records = new ArrayList<>();
   private final boolean skipTokenizationErrors;
   private final boolean tokenize;

   private final List<Record> recordsProcessedWithErrors = new ArrayList<>();

   BaseMultipleRecordsCommand(List<? extends Record> records, boolean skipTokenizationErrors, boolean tokenize) {
      this.records.addAll(records);
      this.skipTokenizationErrors = skipTokenizationErrors;
      this.tokenize = tokenize;
   }

   @Override
   protected void run(CommandCallback<List<Record>> callback) throws Exception {
      List<MultiRequestElement> requestElements = Queryable.from(records)
            .map(this::prepareMultiRequestElements)
            .fold(Collections.emptyList(), (l, r) -> Queryable.concat(l, r).toList());
      if (requestElements.isEmpty()) {
         callback.onSuccess(Collections.emptyList());
         return;
      }

      Observable.from(requestElements)
            .buffer(MULTIFUNCTION_REQUEST_ELEMENTS_LIMIT)
            .map(multiRequestElements -> janet.createPipe(MultifunctionNxtHttpAction.class, Schedulers.io())
                  .createObservableResult(new MultifunctionNxtHttpAction(createRequestBody(multiRequestElements)))
            )
            .reduce(Observable::concatWith)
            .flatMap(httpActionObservable -> httpActionObservable)
            .map(MultifunctionNxtHttpAction::getResponse)
            .toList()
            .map((nxtResponses) -> createResponseBody(this.records, nxtResponses))
            .flatMap(handleMultipleRecordsResult())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   abstract List<MultiRequestElement> prepareMultiRequestElements(Record record);

   private MultiRequestBody createRequestBody(List<MultiRequestElement> requestElements) {
      return ImmutableMultiRequestBody.builder()
            .multiRequestElements(requestElements)
            .build();
   }

   abstract List<NxtRecord> createResponseBody(List<Record> records, List<MultiResponseBody> nxtResponses);

   @NonNull
   private Func1<List<NxtRecord>, Observable<List<Record>>> handleMultipleRecordsResult() {
      return nxtRecords -> {
         List<NxtRecord> resultsProcessedWithErrors = Queryable.from(nxtRecords)
               .filter(nxtRecord -> !nxtRecord.getResponseErrors().isEmpty())
               .toList();
         recordsProcessedWithErrors.addAll(Queryable.from(resultsProcessedWithErrors)
               .map(nxtRecord -> tokenize ? nxtRecord.getTokenizedRecord() : nxtRecord.getDetokenizedRecord())
               .toList());

         if (skipTokenizationErrors || resultsProcessedWithErrors.isEmpty()) {
            return Observable.from(nxtRecords)
                  .filter(nxtRecord -> nxtRecord.getResponseErrors().isEmpty())
                  .map(nxtRecord -> tokenize ? nxtRecord.getTokenizedRecord() : nxtRecord.getDetokenizedRecord())
                  .toList();
         } else {
            List<MultiErrorResponse> errorResponses = Queryable.from(resultsProcessedWithErrors)
                  .map(NxtRecord::getResponseErrors)
                  .fold(Collections.emptyList(), (l, r) -> Queryable.concat(l, r).toList());
            return Observable.error(new NxtMultifunctionException(
                  NxtBankCardHelper.getResponseErrorMessage(errorResponses)));
         }
      };
   }

   @NonNull
   public List<Record> getRecordsProcessedWithErrors() {
      return recordsProcessedWithErrors;
   }

}