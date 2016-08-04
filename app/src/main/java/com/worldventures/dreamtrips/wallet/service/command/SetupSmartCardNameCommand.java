package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.meta.SetMetaDataPairAction;

@CommandAction
public class SetupSmartCardNameCommand extends Command<Void> implements InjectableAction {
    private static final String CARD_NAME_KEY = "card_name";

    @Inject
    @Named(JanetModule.JANET_WALLET)
    Janet janet;

    private final String cardName;

    public SetupSmartCardNameCommand(String cardName) {
        this.cardName = cardName;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        WalletValidateHelper.validateCardNameOrThrow(cardName);

        janet.createPipe(SetMetaDataPairAction.class)
                .createObservableResult(new SetMetaDataPairAction(CARD_NAME_KEY, cardName))
                .subscribe(action -> callback.onSuccess(null), callback::onFail);
    }
}
