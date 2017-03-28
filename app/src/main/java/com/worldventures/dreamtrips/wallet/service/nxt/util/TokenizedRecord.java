package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;

import java.util.Collections;
import java.util.List;

public class TokenizedRecord extends NxtRecordResponse {

   public static TokenizedRecord from(@NonNull Record detokenizedRecord, @NonNull MultiResponseBody nxtResponses) {
      return from(detokenizedRecord, Collections.singletonList(nxtResponses));
   }

   public static TokenizedRecord from(@NonNull Record detokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses) {
      return new TokenizedRecord(detokenizedRecord, nxtResponses);
   }

   private TokenizedRecord(@NonNull Record detokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses) {
      super(detokenizedRecord, nxtResponses);
   }

   @Override
   public Record getTokenizedRecord() {
      return NxtBankCardHelper.getTokenizedRecord(this, refIdPrefix);
   }

   @Override
   public Record getDetokenizedRecord() {
      return record;
   }

}