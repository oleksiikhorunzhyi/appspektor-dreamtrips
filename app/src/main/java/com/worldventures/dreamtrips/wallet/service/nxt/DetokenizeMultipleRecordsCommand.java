package com.worldventures.dreamtrips.wallet.service.nxt;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestElement;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.DetokenizedRecord;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecord;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DetokenizeMultipleRecordsCommand extends BaseMultipleRecordsCommand {

   public DetokenizeMultipleRecordsCommand(List<? extends Record> records, boolean skipTokenizationErrors) {
      super(records, skipTokenizationErrors, false);
   }

   @Override
   List<MultiRequestElement> prepareMultiRequestElements(Record record) {
      return NxtBankCardHelper.getDataForDetokenization(record);
   }

   @Override
   List<NxtRecord> createResponseBody(List<Record> records, List<MultiResponseBody> nxtResponses) {
      return Queryable.from(records)
            .map(record -> DetokenizedRecord.from(record, nxtResponses))
            .cast(NxtRecord.class)
            .toList();
   }

}