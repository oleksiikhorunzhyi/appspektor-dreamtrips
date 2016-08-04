package com.worldventures.dreamtrips.wallet.service.command;

import android.content.Context;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.CardStackViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static io.techery.janet.ActionState.Status.FAIL;
import static io.techery.janet.ActionState.Status.SUCCESS;

@CommandAction
public class CardStacksCommand extends Command<List<CardStackViewModel>> implements InjectableAction {

    @Inject @Named(JANET_WALLET) Janet janet;
    @Inject @ForApplication Context context;

    private volatile List<Card> cachedList = new ArrayList<>();

    @Override protected void run(CommandCallback<List<CardStackViewModel>> callback) throws Throwable {
        janet.createPipe(CardListCommand.class)
                .createObservable(new CardListCommand())
                .doOnNext(it -> {
                    if (it.status == ActionState.Status.PROGRESS) {
                        setCachedList(it.action.getCachedItems());
                        callback.onProgress(0);
                    }
                })
                .filter(it -> it.status == SUCCESS || it.status == FAIL)
                .map(it -> it.action.getResult())
                .map(this::convert)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private List<CardStackViewModel> convert(List<Card> cardList) {
        if (cardList == null) return new ArrayList<>();

        ArrayList<CardStackViewModel> result = new ArrayList<>();
        List<BankCard> creditsBankCards = getBankCardsByType(cardList, BankCard.CardType.CREDIT);
        List<BankCard> debitBankCards = getBankCardsByType(cardList, BankCard.CardType.DEBIT);

        int creditCardTitle = QuantityHelper.chooseResource(creditsBankCards.size(), R.string.wallet_credit_card_title, R.string.wallet_credit_cards_title);
        int debitCardTitle = QuantityHelper.chooseResource(debitBankCards.size(), R.string.wallet_debit_card_title, R.string.wallet_debit_cards_title);

        if (creditsBankCards.size() > 0)
            result.add(new CardStackViewModel(context.getString(creditCardTitle, creditsBankCards.size()), creditsBankCards));
        if (debitBankCards.size() > 0)
            result.add(new CardStackViewModel(context.getString(debitCardTitle, debitBankCards.size()), debitBankCards));
        return result;
    }

    protected List<BankCard> getBankCardsByType(List<Card> result, BankCard.CardType credit) {
        return Queryable.from(result)
                .filter(it -> it instanceof BankCard)
                .map(it -> (BankCard) it)
                .filter(it -> it.cardType() == credit)
                .toList();
    }

    private void setCachedList(List<Card> cachedList) {
        this.cachedList.clear();
        if (cachedList != null) this.cachedList.addAll(cachedList);
    }

    public List<CardStackViewModel> getCachedList() {
        return convert(cachedList);
    }
}
