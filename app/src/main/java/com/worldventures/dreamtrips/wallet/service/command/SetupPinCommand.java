package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class SetupPinCommand extends Command<Void> implements InjectableAction {

    @Inject SnappyRepository snappyRepository;

    private final String smartCardId;

    public SetupPinCommand(String smartCardId) {
        this.smartCardId = smartCardId;
    }

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        Thread.sleep(5000);
        updateCashedSmartCard();
        callback.onSuccess(null);
    }

    private void updateCashedSmartCard() {
        SmartCard smartCard = snappyRepository.getSmartCard(smartCardId);
        smartCard = ImmutableSmartCard.builder()
                .from(smartCard)
                .status(SmartCard.CardStatus.ACTIVE)
                .build();
        snappyRepository.saveSmartCard(smartCard);
        snappyRepository.setActiveSmartCardId(smartCard.getSmartCardId());
    }
}
