package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

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
public class CardConvertorCommand extends Command<List<Card>> {
    @Inject
    @Named(JANET_WALLET)
    Janet janet;

    @Override
    protected void run(CommandCallback<List<Card>> callback) throws Throwable {
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
                                        .type("VISA")
                                        .bankName(record.title())
                                        .build())
                                .toList();
                    }
                })
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
