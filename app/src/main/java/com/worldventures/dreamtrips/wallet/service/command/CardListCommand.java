package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.converter.BankCardConverter;

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

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class CardListCommand extends Command<List<Card>> implements InjectableAction, CachedAction<List<Card>> {

    @Inject
    @Named(JANET_WALLET)
    Janet janet;

    private final BankCardConverter converter = new BankCardConverter();

    private volatile List<Card> cachedItems;

    @Override
    protected void run(CommandCallback<List<Card>> callback) throws Throwable {
        if (!cachedItems.isEmpty()) {
            callback.onProgress(0);
        }
        janet.createPipe(GetMemberRecordsAction.class)
                .createObservableResult(new GetMemberRecordsAction())
                .flatMap(action -> Observable.from(action.records)
                        .map(record -> (Card) converter.from(record))
                        .toList()
                ).subscribe(callback::onSuccess, callback::onFail);
    }

    public List<Card> getCachedItems() {
        return cachedItems;
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
}
