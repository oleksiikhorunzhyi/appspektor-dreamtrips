package com.worldventures.dreamtrips.wallet.analytics.tokenization;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.BaseCardDetailsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;

public class TokenizationCardAction extends BaseCardDetailsAction {

   @Attribute("coordinates") String coordinates;

   private final ActionType actionType;
   private final boolean tokenize;

   public static TokenizationCardAction from(Record record, boolean success, ActionType actionType, boolean tokenize) {
      if (success) {
         return new TokenizeSuccessAction(record, actionType, tokenize);
      } else {
         return new TokenizeErrorAction(record, actionType, tokenize);
      }
   }

   TokenizationCardAction(Record record, ActionType actionType, boolean tokenize) {
      this.actionType = actionType;
      this.tokenize = tokenize;

      fillRecordDetails(record);
   }

   void setCoordinates(@Nullable WalletCoordinates coordinates) {
      if (coordinates != null) this.coordinates = String.format("%s,%s", coordinates.lat(), coordinates.lng());
   }

   String generateCondition() {
      return String.format("%s Payment Card %s",
            actionType.getTypeLabel(), (tokenize ? "Tokenization" : "Detokenization"));
   }

}