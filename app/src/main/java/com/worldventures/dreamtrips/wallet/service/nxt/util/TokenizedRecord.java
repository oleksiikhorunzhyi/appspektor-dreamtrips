package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;

import java.util.Collections;
import java.util.List;

public class TokenizedRecord extends NxtRecordResponse {

   public static TokenizedRecord from(@NonNull Record detokenizedRecord, @NonNull MultiResponseBody nxtResponses) {
      return from(detokenizedRecord, Collections.singletonList(nxtResponses), null);
   }

   public static TokenizedRecord from(@NonNull Record detokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses) {
      return from(detokenizedRecord, nxtResponses, null);
   }

   public static TokenizedRecord from(@NonNull Record detokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses, String refIdPrefix) {
      return new TokenizedRecord(detokenizedRecord, nxtResponses, refIdPrefix);
   }

   private TokenizedRecord(@NonNull Record detokenizedRecord, @NonNull List<MultiResponseBody> nxtResponses, @Nullable String refIdPrefix) {
      super(detokenizedRecord, nxtResponses, refIdPrefix);
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