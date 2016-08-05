package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.BankCardToRecordConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.GetMemberRecordsAction;
import io.techery.janet.smartcard.model.Record;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class CardListCommand extends Command<List<Card>> implements InjectableAction, CachedAction<List<Card>> {

    @Inject
    @Named(JANET_WALLET)
    Janet janet;

    private final BankCardToRecordConverter converter = new BankCardToRecordConverter();

    private volatile List<Card> cachedItems;

    @Override
    protected void run(CommandCallback<List<Card>> callback) throws Throwable {
        if (!cachedItems.isEmpty()) {
            callback.onProgress(0);
        }
        janet.createPipe(GetMemberRecordsAction.class)
                .createObservableResult(new GetMemberRecordsAction())
                .flatMap(action -> {
                    if (action.records.isEmpty()) return createFakeRecords(action);
                    else return Observable.just(action);
                })
                .flatMap(action -> Observable.from(action.records)
                        .map(record -> (Card) converter.convert(record))
                        .toList()
                ).subscribe(callback::onSuccess, callback::onFail);
    }

    private Observable<GetMemberRecordsAction> createFakeRecords(GetMemberRecordsAction action) {
        ActionPipe<AttachCardCommand> pipe = janet.createPipe(AttachCardCommand.class);
        ArrayList<Observable<?>> ws = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < 8; i++) {
            BankCard bankCard = ImmutableBankCard.builder()
                    .number(Math.abs(random.nextLong()) % 1000000000000000L)
                    .title("Jane's card" + (i + 1))
                    .type(Record.FinancialService.MASTERCARD)
                    .cvv(random.nextInt(1000))
                    .expiryMonth(random.nextInt(13))
                    .expiryYear(random.nextInt(100))
                    .build();

            ws.add(pipe.createObservableResult(new AttachCardCommand(bankCard)));
        }
        return Observable.zip(ws, args -> {
            action.records.clear();
            for (Object arg : args) {
                Record record = ((AttachCardCommand) arg).getResult();
                ((List<Record>) action.records).add(record);
            }
            return action;
        });
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
        CacheBundle bundle = new CacheBundleImpl();
        return ImmutableCacheOptions.builder()
                .params(bundle)
                .build();
    }
}
