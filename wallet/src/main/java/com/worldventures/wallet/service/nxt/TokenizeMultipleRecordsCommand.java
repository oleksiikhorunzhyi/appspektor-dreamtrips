package com.worldventures.wallet.service.nxt;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.service.nxt.model.MultiRequestElement;
import com.worldventures.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.wallet.service.nxt.util.NxtRecord;
import com.worldventures.wallet.service.nxt.util.TokenizedRecord;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class TokenizeMultipleRecordsCommand extends BaseMultipleRecordsCommand {

   public TokenizeMultipleRecordsCommand(List<? extends Record> records, boolean skipTokenizationErrors) {
      super(records, skipTokenizationErrors, true);
   }

   @Override
   List<MultiRequestElement> prepareMultiRequestElements(Record record) {
      return NxtBankCardHelper.getDataForTokenization(record);
   }

   @Override
   List<NxtRecord> createResponseBody(List<Record> records, List<MultiResponseBody> nxtResponses) {
      return Queryable.from(records)
            .map(record -> TokenizedRecord.from(record, nxtResponses))
            .cast(NxtRecord.class)
            .toList();
   }

}