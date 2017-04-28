package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;

import java.util.Collections;
import java.util.List;

public class DetokenizedRecord extends NxtRecordResponse {

   public static DetokenizedRecord from(@NonNull Record tokenizedRecord, @NonNull MultiResponseBody nxtResponses) {
      return from(tokenizedRecord, Collections.singletonList(nxtResponses));
   }

   public static DetokenizedRecord from(@NonNull Record tokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses) {
      return new DetokenizedRecord(tokenizedRecord, nxtResponses);
   }

   private DetokenizedRecord(@NonNull Record tokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses) {
      super(tokenizedRecord, nxtResponses);
   }

   @Override
   public Record getTokenizedRecord() {
      return record;
   }

   @Override
   public Record getDetokenizedRecord() {
      return NxtBankCardHelper.getDetokenizedRecord(this, refIdPrefix);
   }

}