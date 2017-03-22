package com.worldventures.dreamtrips.wallet.util;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;

public class SmartCardInteractorHelper {

   private SmartCardInteractor smartCardInteractor;

   @Inject
   public SmartCardInteractorHelper(SmartCardInteractor smartCardInteractor) {
      this.smartCardInteractor = smartCardInteractor;
   }

   public void sendSingleDefaultCardTask(Action1<Record> action, Observable.Transformer<Record, Record> composer) {
      smartCardInteractor.defaultRecordIdPipe()
            .createObservableResult(DefaultRecordIdCommand.fetch())
            .flatMap(defaultRecIdCommand -> smartCardInteractor.cardsListPipe()
                  .createObservableResult(RecordListCommand.fetch())
                  .map(recordsCommand -> findRecord(recordsCommand.getResult(), defaultRecIdCommand.getResult())))
            .compose(composer)
            .subscribe(action, throwable -> {
            });
   }

   private Record findRecord(List<Record> records, String recordId) {
      return Queryable.from(records).firstOrDefault(rec -> TextUtils.equals(rec.id(), recordId));
   }
}
