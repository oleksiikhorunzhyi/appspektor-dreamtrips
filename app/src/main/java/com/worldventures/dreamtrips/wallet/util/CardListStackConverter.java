package com.worldventures.dreamtrips.wallet.util;


import android.content.Context;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.BankCardViewModel;

import java.util.ArrayList;
import java.util.List;

public class CardListStackConverter {

   private final Context context;

   public CardListStackConverter(Context context) {
      this.context = context;
   }

   public List<CardStackViewModel> convertToModelViews(List<CardStacksCommand.CardStackModel> stackList) {
      int sourceLength = stackList.size();
      List<CardStackViewModel> list = new ArrayList<>(sourceLength);
      BankCard defaultBankCard = findDefaultCard(stackList);

      for (CardStacksCommand.CardStackModel vm: stackList){
         switch (vm.type()) {
            case DEBIT:
               list.add(obtainDebitCardsStack(vm.bankCards(), defaultBankCard));
               break;
            case CREDIT:
               list.add(obtainCreditCardsStack(vm.bankCards(), defaultBankCard));
               break;
            default:
               list.add(obtainDefaultCardStack(vm.bankCards()));
         }
      }

      return list;
   }

   private CardStackViewModel obtainDebitCardsStack(List<BankCard> bankCards, BankCard defaultBankCard){
      int debitCardListSize = bankCards.size();
      int debitTitleId = QuantityHelper.chooseResource(debitCardListSize, R.string.wallet_debit_card_title, R.string.wallet_debit_cards_title);
      String title = context.getString(debitTitleId, debitCardListSize);
      List<BankCardViewModel> debitCardsViewModels = convertToCardViewModels(bankCards, defaultBankCard);

      return new CardStackViewModel(CardStacksCommand.CardStackModel.StackType.DEBIT, debitCardsViewModels, title);
   }

   private CardStackViewModel obtainCreditCardsStack(List<BankCard> bankCards, BankCard defaultBankCard){
      int creditCardListSize = bankCards.size();
      int creditTitleId = QuantityHelper.chooseResource(creditCardListSize, R.string.wallet_credit_card_title, R.string.wallet_credit_cards_title);
      String title = context.getString(creditTitleId, creditCardListSize);
      List<BankCardViewModel> debitCardsViewModels = convertToCardViewModels(bankCards, defaultBankCard);

      return new CardStackViewModel(CardStacksCommand.CardStackModel.StackType.CREDIT, debitCardsViewModels, title);
   }

   private CardStackViewModel obtainDefaultCardStack(List<BankCard> bankCards) {
      String title = context.getString(R.string.dashboard_default_card_stack_title);
      List<BankCardViewModel> defaultCardsViewModels = Queryable.from(bankCards)
            .map(element -> new BankCardViewModel(element, false)).toList();

      return new CardStackViewModel(CardStacksCommand.CardStackModel.StackType.DEFAULT, defaultCardsViewModels, title);
   }

   private BankCard findDefaultCard(List<CardStacksCommand.CardStackModel> models) {
      CardStacksCommand.CardStackModel defaultStack = Queryable.from(models).firstOrDefault(element -> element.type() == CardStacksCommand.CardStackModel.StackType.DEFAULT);
      return defaultStack != null && defaultStack.bankCards().size() > 0? defaultStack.bankCards().get(0) : null;
   }

   private List<BankCardViewModel> convertToCardViewModels(List<BankCard> bankCards, BankCard defaultCard) {
      return Queryable.from(bankCards)
            .map(element -> new BankCardViewModel(element, defaultCard != null && TextUtils.equals(element.id(), defaultCard.id())))
            .toList();
   }

}
