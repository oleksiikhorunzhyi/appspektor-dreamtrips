package com.worldventures.dreamtrips.wallet.analytics.tokenization;

import com.worldventures.dreamtrips.wallet.analytics.BaseCardDetailsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCard;

public class TokenizationCardAction extends BaseCardDetailsAction {

   private final ActionType actionType;
   private final boolean tokenize;

   public static TokenizationCardAction from(NxtBankCard nxtBankCard, ActionType actionType, boolean tokenize) {
      if (nxtBankCard.getResponseErrors().isEmpty()) {
         return new TokenizeSuccessAction(nxtBankCard.getTokenizedBankCard(), actionType, tokenize);
      } else {
         return new TokenizeErrorAction(nxtBankCard.getTokenizedBankCard(), actionType, tokenize);
      }
   }

   TokenizationCardAction(BankCard bankCard, ActionType actionType, boolean tokenize) {
      this.actionType = actionType;
      this.tokenize = tokenize;

      fillPaycardInfo(bankCard);
   }

   String generateCondition() {
      return String.format("%s Payment Card %s",
            actionType.getTypeLabel(), (tokenize ? "Tokenization" : "Detokenization"));
   }

}