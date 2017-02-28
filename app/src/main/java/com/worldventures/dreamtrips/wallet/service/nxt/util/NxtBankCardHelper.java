package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestElement;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiErrorResponse;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestElement;

import java.util.ArrayList;
import java.util.List;

public class NxtBankCardHelper {

   private static final String OPERATION_TOKENIZE = "tokenize";
   private static final String OPERATION_DETOKENIZE = "detokenize";

   private static final String TOKEN_NAME_GENERIC = "smartcardgeneric";
   private static final String TOKEN_NAME_CC = "smartcardcc";

   private static final String PAN = "number";
   private static final String CVV = "cvv";
   private static final String TRACK_1 = "track1";
   private static final String TRACK_2 = "track2";
   private static final String TRACK_3 = "track3";

   private NxtBankCardHelper() {
   }

   public static List<MultiRequestElement> getDataForTokenization(BankCard bankCard) {
      return getDataForTokenization(bankCard, null);
   }

   public static List<MultiRequestElement> getDataForTokenization(BankCard bankCard, @Nullable String refIdPrefix) {
      List<MultiRequestElement> elements = new ArrayList<>();

      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_TOKENIZE).tokenName(TOKEN_NAME_CC)
            .value(bankCard.number()).referenceId(prefixRefId(PAN, refIdPrefix))
            .build());
      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_TOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(String.valueOf(bankCard.cvv())).referenceId(prefixRefId(CVV, refIdPrefix))
            .build());

      safelyAddEncodedElement(elements, OPERATION_TOKENIZE, TOKEN_NAME_GENERIC, bankCard.track1(), prefixRefId(TRACK_1, refIdPrefix));
      safelyAddEncodedElement(elements, OPERATION_TOKENIZE, TOKEN_NAME_GENERIC, bankCard.track2(), prefixRefId(TRACK_2, refIdPrefix));
      safelyAddEncodedElement(elements, OPERATION_TOKENIZE, TOKEN_NAME_GENERIC, bankCard.track3(), prefixRefId(TRACK_3, refIdPrefix));

      return elements;
   }

   public static List<MultiRequestElement> getDataForDetokenization(BankCard bankCard) {
      return getDataForDetokenization(bankCard, null);
   }

   public static List<MultiRequestElement> getDataForDetokenization(BankCard bankCard, @Nullable String refIdPrefix) {
      List<MultiRequestElement> elements = new ArrayList<>();

      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_DETOKENIZE).tokenName(TOKEN_NAME_CC)
            .value(bankCard.number()).referenceId(prefixRefId(PAN, refIdPrefix))
            .build());
      elements.add(ImmutableMultiRequestElement.builder()
            .operation(OPERATION_DETOKENIZE).tokenName(TOKEN_NAME_GENERIC)
            .value(bankCard.cvvToken()).referenceId(prefixRefId(CVV, refIdPrefix))
            .build());

      safelyAddElement(elements, OPERATION_DETOKENIZE, TOKEN_NAME_GENERIC, bankCard.track1(), prefixRefId(TRACK_1, refIdPrefix));
      safelyAddElement(elements, OPERATION_DETOKENIZE, TOKEN_NAME_GENERIC, bankCard.track2(), prefixRefId(TRACK_2, refIdPrefix));
      safelyAddElement(elements, OPERATION_DETOKENIZE, TOKEN_NAME_GENERIC, bankCard.track3(), prefixRefId(TRACK_3, refIdPrefix));

      return elements;
   }

   public static BankCard getTokenizedBankCard(TokenizedBankCard card, @Nullable String refIdPrefix) {
      return ImmutableBankCard.builder().from(card.bankCard)
            // Save tokenized cvv as String value and also clear real cvv
            .cvv(0)
            .cvvToken(card.nxtValues.get(prefixRefId(CVV, refIdPrefix)))
            // Save tokenized track values into original values
            .number(card.nxtValues.get(prefixRefId(PAN, refIdPrefix)))
            .track1(card.nxtValues.get(prefixRefId(TRACK_1, refIdPrefix)))
            .track2(card.nxtValues.get(prefixRefId(TRACK_2, refIdPrefix)))
            .track3(card.nxtValues.get(prefixRefId(TRACK_3, refIdPrefix)))
            .build();
   }

   public static BankCard getDetokenizedBankCard(DetokenizedBankCard card) {
      return ImmutableBankCard.builder().from(card.bankCard)
            .number(card.nxtValues.get(PAN))
            .cvv(Integer.parseInt(card.nxtValues.get(CVV)))
            .cvvToken(null)
            .track1(getDecodedElement(card.nxtValues.get(TRACK_1)))
            .track2(getDecodedElement(card.nxtValues.get(TRACK_2)))
            .track3(getDecodedElement(card.nxtValues.get(TRACK_3)))
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

   private static String prefixRefId(String refId, @Nullable String prefix) {
      return (prefix == null) ? refId : String.format("%s_%s", prefix, refId);
   }

   @NonNull
   public static List<MultiErrorResponse> getResponseErrors(NxtBankCardResponse response, @Nullable String refIdPrefix) {
      return Queryable.from(new String[]{PAN, CVV, TRACK_1, TRACK_2, TRACK_3})
            .map(refId -> response.nxtErrors.get(prefixRefId(refId, refIdPrefix)))
            .notNulls()
            .toList();
   }

}