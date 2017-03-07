package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;

import java.util.Collections;
import java.util.List;

public class DetokenizedRecord extends NxtRecordResponse {

   public static DetokenizedRecord from(@NonNull Record tokenizedRecord, @NonNull MultiResponseBody nxtResponses) {
      return from(tokenizedRecord, Collections.singletonList(nxtResponses), null);
   }

   public static DetokenizedRecord from(@NonNull Record tokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses) {
      return new DetokenizedRecord(tokenizedRecord, nxtResponses, null);
   }

   public static DetokenizedRecord from(@NonNull Record tokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses, String refIdPrefix) {
      return new DetokenizedRecord(tokenizedRecord, nxtResponses, refIdPrefix);
   }

   private DetokenizedRecord(@NonNull Record tokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses, @Nullable String refIdPrefix) {
      super(tokenizedRecord, nxtResponses, refIdPrefix);
   }

   @Override
   public Record getTokenizedRecord() {
      return record;
   }

   @Override
   public Record getDetokenizedRecord() {
      return NxtBankCardHelper.getDetokenizedRecord(this);
   }

}