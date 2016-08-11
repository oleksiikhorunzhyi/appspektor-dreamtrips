package com.worldventures.dreamtrips.wallet.service.command;

import android.content.Context;
import android.util.Pair;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionState;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class CardStacksCommand extends Command<List<CardStackViewModel>> implements InjectableAction {

    @Inject
    @Named(JANET_WALLET)
    Janet janet;
    @Inject
    @ForApplication
    Context context;

    private volatile List<CardStackViewModel> cachedList = new ArrayList<>();

    @Override
    protected void run(CommandCallback<List<CardStackViewModel>> callback) throws Throwable {
        Observable.combineLatest(
                janet.createPipe(CardListCommand.class).createObservable(new CardListCommand()),
                janet.createPipe(FetchDefaultCardCommand.class).createObservable(new FetchDefaultCardCommand()),
                Pair::new)
                .doOnNext(pair -> {
                    if (pair.first.status == ActionState.Status.PROGRESS && pair.second.status == ActionState.Status.PROGRESS) {
                        setCachedList(convert(pair.first.action.getCacheData(), pair.second.action.getCachedResult()));
                        callback.onProgress(0);
                    }
                })
                .filter(pair -> (pair.first.status == ActionState.Status.SUCCESS && pair.second.status == ActionState.Status.SUCCESS)
                        || pair.first.status == ActionState.Status.FAIL
                        || pair.second.status == ActionState.Status.FAIL)
                .flatMap(pair -> {
                    if (pair.first.status == ActionState.Status.FAIL) {
                        return Observable.error(pair.first.exception);
                    } else if (pair.second.status == ActionState.Status.FAIL) {
                        return Observable.error(pair.second.exception);
                    } else {
                        return Observable.just(convert(pair.first.action.getResult(), pair.second.action.getResult()));
                    }
                })
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private List<CardStackViewModel> convert(List<Card> cardList, String defaultCardId) {
        if (cardList == null) return new ArrayList<>();
        ArrayList<CardStackViewModel> result = new ArrayList<>();

        CardStackViewModel stack = defaultCardStack((BankCard) findDefaultCard(cardList, defaultCardId));
        if (stack != null) result.add(stack);

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

    private Card findDefaultCard(List<Card> cardList, String defaultCardId) {
        if (defaultCardId == null) {
            return null;
        }
        return Queryable.from(cardList).firstOrDefault(
                card -> card != null && card.id() != null && defaultCardId.equals(card.id())
        );
    }

    private CardStackViewModel defaultCardStack(BankCard defaultCard) {
        if (defaultCard == null) return null;
        ArrayList<BankCard> bankCards = new ArrayList<>();
        bankCards.add(defaultCard);
        return new CardStackViewModel(context.getString(R.string.dashboard_default_card_stack_title), bankCards);
    }

    protected List<BankCard> getBankCardsByType(List<Card> cardList, BankCard.CardType credit) {
        if (cardList == null) return Collections.emptyList();
        return Queryable.from(cardList)
                .filter(it -> it instanceof BankCard)
                .map(it -> (BankCard) it)
                .filter(it -> it.cardType() == credit)
                .toList();
    }

    private void setCachedList(List<CardStackViewModel> cachedList) {
        this.cachedList.clear();
        if (cachedList != null) this.cachedList.addAll(cachedList);
    }

    public List<CardStackViewModel> getCachedList() {
        return cachedList;
    }

}
