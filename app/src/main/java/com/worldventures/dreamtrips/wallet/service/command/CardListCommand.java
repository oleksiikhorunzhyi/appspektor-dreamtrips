package com.worldventures.dreamtrips.wallet.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.converter.BankCardConverter;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.GetMemberRecordsAction;
import rx.Observable;
import rx.functions.Func1;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class CardListCommand extends Command<List<Card>> implements InjectableAction, CachedAction<List<Card>> {

    @Inject
    @Named(JANET_WALLET)
    Janet janet;

    private final BankCardConverter converter = new BankCardConverter();

    private Func1<List<Card>, Observable<List<Card>>> operationFunc;
    private boolean forceUpdate;

    private volatile List<Card> cachedItems;

    public static CardListCommand get(boolean forceUpdate) {
        return new CardListCommand(forceUpdate);
    }

    public static CardListCommand remove(String cardId) {
        return new CardListCommand(new RemoveOperationFunc(cardId));
    }

    public static CardListCommand add(Card card) {
        return new CardListCommand(new AddOperationFunc(card));
    }

    private CardListCommand(Func1<List<Card>, Observable<List<Card>>> operationFunc) {
        this.operationFunc = operationFunc;
    }

    private CardListCommand(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    @Override
    protected void run(CommandCallback<List<Card>> callback) throws Throwable {
        Observable<List<Card>> listObservable =
                !isCachePresent() || forceUpdate ? fetchFromDevice() : Observable.just(cachedItems);

        if (operationFunc != null) {
            listObservable = listObservable.flatMap(operationFunc);
        }
        listObservable.subscribe(callback::onSuccess, callback::onFail);
    }

    private Observable<List<Card>> fetchFromDevice() {
        return janet.createPipe(GetMemberRecordsAction.class)
                .createObservableResult(new GetMemberRecordsAction())
                .flatMap(action -> Observable.from(action.records)
                        .map(record -> (Card) converter.from(record))
                        .toList());
    }

    @Override
    public List<Card> getCacheData() {
        return getResult();
    }

    @Override
    public void onRestore(ActionHolder holder, List<Card> cache) {
        if (cache == null) cachedItems = Collections.emptyList();
        else cachedItems = cache;
    }

    @Override
    public CacheOptions getCacheOptions() {
        return ImmutableCacheOptions.builder().build();
    }

    private static final class RemoveOperationFunc implements Func1<List<Card>, Observable<List<Card>>> {
        private String cardId;

        public RemoveOperationFunc(String cardId) {
            this.cardId = cardId;
        }

        @Override
        public Observable<List<Card>> call(List<Card> cards) {
            cards.remove(Queryable.from(cards)
                    .first(element -> element.id().equals(cardId)));
            return Observable.just(cards);
        }
    }

    private static final class AddOperationFunc implements Func1<List<Card>, Observable<List<Card>>> {
        private Card card;

        public AddOperationFunc(Card card) {
            this.card = card;
        }

        @Override
        public Observable<List<Card>> call(List<Card> cards) {
            cards.add(card);
            return Observable.just(cards);
        }
    }

    private boolean isCachePresent() {
        return cachedItems != null && !cachedItems.isEmpty();
    }
}