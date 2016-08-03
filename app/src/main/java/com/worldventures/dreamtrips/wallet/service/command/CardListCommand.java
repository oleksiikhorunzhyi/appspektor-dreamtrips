package com.worldventures.dreamtrips.wallet.service.command;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import io.techery.janet.smartcard.action.records.GetMemberRecordsAction;
import io.techery.janet.smartcard.model.Record;
import rx.Observable;
import rx.functions.Func1;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class CardListCommand extends Command<List<Card>> implements InjectableAction, CachedAction<List<Card>> {

    @Inject @Named(JANET_WALLET) Janet janet;

    private List<Card> cachedItems = new ArrayList<>();

    @Override
    protected void run(CommandCallback<List<Card>> callback) throws Throwable {
        if (!cachedItems.isEmpty()) {
            callback.onProgress(0);
        }
        janet.createPipe(GetMemberRecordsAction.class)
                .createObservable(new GetMemberRecordsAction())
                .compose(new ActionStateToActionTransformer<>())
                .map(memberRecordsAction -> memberRecordsAction.records)
                .flatMap(new Func1<List<? extends Record>, Observable<List<Card>>>() {
                    @Override
                    public Observable<List<Card>> call(List<? extends Record> records) {
                        return Observable.from(records)
                                .map((Func1<Record, Card>) record -> ImmutableBankCard.builder()
                                        .number(record.csc())
                                        .type("VISA") //todo
                                        .cardType(generateRandomCardType())
                                        .title(record.title())
                                        .build())
                                .toList();
                    }
                })
                .subscribe(callback::onSuccess, callback::onFail);
    }

    public List<Card> getCachedItems() {
        return cachedItems;
    }

    //todo
    @NonNull protected BankCard.CARD_TYPE generateRandomCardType() {
        return new Random().nextBoolean() ? BankCard.CARD_TYPE.CREDIT : BankCard.CARD_TYPE.DEBIT;
    }

    @Override public List<Card> getCacheData() {
        return getResult();
    }

    @Override public void onRestore(ActionHolder holder, List<Card> cache) {
        if (cache == null) cachedItems = new ArrayList<>();
        else cachedItems = cache;
    }

    @Override public CacheOptions getCacheOptions() {
        CacheBundle bundle = new CacheBundleImpl();
        return ImmutableCacheOptions.builder()
                .params(bundle)
                .build();
    }
}
