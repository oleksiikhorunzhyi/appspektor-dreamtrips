package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestElement;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiErrorResponse;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestElement;

import java.util.ArrayList;
import java.util.List;

public class NxtBankCardHelper {

   public static final String PAN = "number";
   public static final String CVV = "cvv";
   public static final String TRACK_1 = "track1";
   public static final String TRACK_2 = "track2";
   public static final String TRACK_3 = "track3";


   private static final String OPERATION_TOKENIZE = "tokenize";
   private static final String OPERATION_DETOKENIZE = "detokenize";

   private static final String TOKEN_NAME_GENERIC = "smartcardgeneric";

   private NxtBankCardHelper() {
   }

   public static List<MultiRequestElement> getDataForTokenization(Record record) {
      return getDataForTokenization(record, record.id());
   }

   public static List<MultiRequestElement> getDataForTokenization(Record record, @Nullable String refIdPrefix) {
      List<MultiRequestElement> elements = new ArrayList<>();

      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_TOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(record.number()).referenceId(prefixRefId(PAN, refIdPrefix))
            .build());
      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_TOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(record.cvv()).referenceId(prefixRefId(CVV, refIdPrefix))
            .build());

      safelyAddEncodedElement(elements, OPERATION_TOKENIZE, TOKEN_NAME_GENERIC, record.track1(), prefixRefId(TRACK_1, refIdPrefix));
      safelyAddEncodedElement(elements, OPERATION_TOKENIZE, TOKEN_NAME_GENERIC, record.track2(), prefixRefId(TRACK_2, refIdPrefix));
      safelyAddEncodedElement(elements, OPERATION_TOKENIZE, TOKEN_NAME_GENERIC, record.track3(), prefixRefId(TRACK_3, refIdPrefix));

      return elements;
   }

   public static List<MultiRequestElement> getDataForDetokenization(Record record) {
      return getDataForDetokenization(record, record.id());
   }

   public static List<MultiRequestElement> getDataForDetokenization(Record record, @Nullable String refIdPrefix) {
      List<MultiRequestElement> elements = new ArrayList<>();

      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_DETOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(record.number()).referenceId(prefixRefId(PAN, refIdPrefix))
            .build());
      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_DETOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(record.cvv()).referenceId(prefixRefId(CVV, refIdPrefix))
            .build());

      safelyAddElement(elements, OPERATION_DETOKENIZE, TOKEN_NAME_GENERIC, record.track1(), prefixRefId(TRACK_1, refIdPrefix));
      safelyAddElement(elements, OPERATION_DETOKENIZE, TOKEN_NAME_GENERIC, record.track2(), prefixRefId(TRACK_2, refIdPrefix));
      safelyAddElement(elements, OPERATION_DETOKENIZE, TOKEN_NAME_GENERIC, record.track3(), prefixRefId(TRACK_3, refIdPrefix));

      return elements;
   }

   public static Record getTokenizedRecord(TokenizedRecord card, @Nullable String refIdPrefix) {
      return ImmutableRecord.builder().from(card.record)
            // Save tokenized track values into original values
            .number(card.nxtValues.get(prefixRefId(PAN, refIdPrefix)))
            .cvv(card.nxtValues.get(prefixRefId(CVV, refIdPrefix)))
            .track1(card.nxtValues.get(prefixRefId(TRACK_1, refIdPrefix)))
            .track2(card.nxtValues.get(prefixRefId(TRACK_2, refIdPrefix)))
            .track3(card.nxtValues.get(prefixRefId(TRACK_3, refIdPrefix)))
            .build();
   }

   public static Record getDetokenizedRecord(DetokenizedRecord card, @Nullable String refIdPrefix) {
      return ImmutableRecord.builder().from(card.record)
            .number(card.nxtValues.get(prefixRefId(PAN, refIdPrefix)))
            .cvv(card.nxtValues.get(prefixRefId(CVV, refIdPrefix)))
            .track1(getDecodedElement(card.nxtValues.get(prefixRefId(TRACK_1, refIdPrefix))))
            .track2(getDecodedElement(card.nxtValues.get(prefixRefId(TRACK_2, refIdPrefix))))
            .track3(getDecodedElement(card.nxtValues.get(prefixRefId(TRACK_3, refIdPrefix))))
            .build();
   }

   @Nullable
   private static String getDecodedElement(@Nullable String value) {
      if (!TextUtils.isEmpty(value)) {
         return new String(Base64.decode(value.getBytes(), Base64.DEFAULT));
      }
      return null;
   }

   private static void safelyAddElement(List<MultiRequestElement> list,
         String operation, String tokenName, @Nullable String value, String refId) {
      if (!TextUtils.isEmpty(value)) list.add(ImmutableMultiRequestElement.builder()
            .operation(operation).tokenName(tokenName)
            .value(value).referenceId(refId)
            .build());
   }

   private static void safelyAddEncodedElement(List<MultiRequestElement> list,
         String operation, String tokenName, @Nullable String value, String refId) {
      if (!TextUtils.isEmpty(value)) {
         byte[] encodedValue = Base64.encode(value.getBytes(), Base64.DEFAULT);
         list.add(ImmutableMultiRequestElement.builder()
               .operation(operation).tokenName(tokenName)
               .value(new String(encodedValue)).referenceId(refId)
               .build());
      }
   }

   public static String prefixRefId(String refId, @Nullable String prefix) {
      return (prefix == null) ? refId : String.format("%s_%s", prefix, refId);
   }

   @NonNull
   public static List<MultiErrorResponse> getResponseErrors(NxtRecordResponse response, @Nullable String refIdPrefix) {
      return Queryable.from(new String[]{PAN, CVV, TRACK_1, TRACK_2, TRACK_3})
            .map(refId -> response.nxtErrors.get(prefixRefId(refId, refIdPrefix)))
            .notNulls()
            .toList();
   }

   @Nullable
   public static String getResponseErrorMessage(@Nullable List<MultiErrorResponse> errorResponseList) {
      if (errorResponseList == null || errorResponseList.isEmpty()) return null;

      StringBuilder sb = new StringBuilder("[");
      for (int i = 0; i < errorResponseList.size(); i++) {
         if (i > 0) sb.append(", ");
         MultiErrorResponse errorResponse = errorResponseList.get(i);
         sb.append(String.format("\"%s\" : \"%s\"", errorResponse.code(), errorResponse.message()));
      }
      sb.append("]");

      return sb.toString();
   }

}