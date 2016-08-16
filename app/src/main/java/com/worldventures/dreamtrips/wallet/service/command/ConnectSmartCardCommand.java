package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.support.ConnectAction;
import rx.Observable;

@CommandAction
public class ConnectSmartCardCommand extends Command<SmartCard> implements InjectableAction, SmartCardModifier {

    @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
    @Inject SnappyRepository snappyRepository;

    private final SmartCard activeSmartCard;

    public ConnectSmartCardCommand(SmartCard activeSmartCard) {
        this.activeSmartCard = activeSmartCard;
    }

    @Override
    protected void run(CommandCallback<SmartCard> callback) throws Throwable {
        janet.createPipe(ConnectAction.class)
                .createObservableResult(new ConnectAction(activeSmartCard.deviceName(), activeSmartCard.deviceAddress()))
                .flatMap(action -> fetchTechnicalProperties())
                .doOnNext(snappyRepository::saveSmartCard)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    public Observable<SmartCard> fetchTechnicalProperties() {
        return janet.createPipe(FetchCardPropertiesCommand.class)
                .createObservableResult(new FetchCardPropertiesCommand(activeSmartCard))
                .map(Command::getResult);
    }

    @Override
    public SmartCard smartCard() {
        return getResult();
    }
}
