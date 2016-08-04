package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
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

    @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
    @Inject SnappyRepository snappyRepository;

    private final String cardName;
    private final String cardId;

    public SetupSmartCardNameCommand(String cardName, String cardId) {
        this.cardName = cardName;
        this.cardId = cardId;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        WalletValidateHelper.validateCardNameOrThrow(cardName);

        janet.createPipe(SetMetaDataPairAction.class)
                .createObservableResult(new SetMetaDataPairAction(CARD_NAME_KEY, cardName))
                .doOnNext(action -> updateCashedSmartCard())
                .subscribe(action -> callback.onSuccess(null), callback::onFail);
    }

    private void updateCashedSmartCard() {
        SmartCard smartCard = snappyRepository.getSmartCard(cardId);
        smartCard = ImmutableSmartCard.builder()
                .from(smartCard)
                .cardName(cardName)
                .build();
        snappyRepository.saveSmartCard(smartCard);
    }

    public String getCardId() {
        return cardId;
    }
}
