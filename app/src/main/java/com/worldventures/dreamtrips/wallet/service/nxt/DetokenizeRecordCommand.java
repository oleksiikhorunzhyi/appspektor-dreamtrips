package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestElement;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.DetokenizedRecord;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecord;

import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DetokenizeRecordCommand extends BaseRecordCommand {

   public DetokenizeRecordCommand(Record record) {
      super(record, false);
   }

   @Override
   List<MultiRequestElement> prepareMultiRequestElements(Record record) {
      return NxtBankCardHelper.getDataForDetokenization(record);
   }

   @Override
   NxtRecord createResponseBody(Record record, MultiResponseBody nxtResponse) {
      return DetokenizedRecord.from(record, nxtResponse);
   }

}