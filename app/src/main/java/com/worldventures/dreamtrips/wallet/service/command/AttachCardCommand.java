package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.BankCardToRecordConverter;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;
import io.techery.janet.smartcard.action.records.AddRecordAction;
import io.techery.janet.smartcard.model.Record;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class AttachCardCommand extends Command<Record> implements InjectableAction {

    @Inject
    @Named(JANET_WALLET)
    Janet janet;

    private final BankCard card;

    public AttachCardCommand(BankCard card) {
        this.card = card;
    }

    @Override protected void run(CommandCallback<Record> callback) throws Throwable {
        Record record = BankCardToRecordConverter.convert(card);
        janet.createPipe(AddRecordAction.class)
                .createObservable(new AddRecordAction(record))
                .compose(new ActionStateToActionTransformer<>())
                .map(it -> it.record)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
