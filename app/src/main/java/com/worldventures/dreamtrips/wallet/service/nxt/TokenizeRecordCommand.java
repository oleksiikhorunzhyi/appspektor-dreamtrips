package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestElement;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecord;
import com.worldventures.dreamtrips.wallet.service.nxt.util.TokenizedRecord;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class TokenizeRecordCommand extends BaseRecordCommand {

   public TokenizeRecordCommand(Record record) {
      super(record, true);
   }

   @Override
   List<MultiRequestElement> prepareMultiRequestElements(Record record) {
      return NxtBankCardHelper.getDataForTokenization(record);
   }

   @Override
   NxtRecord createResponseBody(Record record, MultiResponseBody nxtResponse) {
      return TokenizedRecord.from(record, nxtResponse);
   }

}