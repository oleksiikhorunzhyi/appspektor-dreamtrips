package com.messenger.delegate.chat.flagging;

import com.messenger.messengerservers.MessengerServerFacade;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FlagMessageCommand extends CommandActionBase<FlagMessageDTO> implements InjectableAction {

    @Inject
    MessengerServerFacade messengerServerFacade;

    private final FlagMessageDTO flagMessageDTO;

    public FlagMessageCommand(FlagMessageDTO flagMessageDTO) {
        this.flagMessageDTO = flagMessageDTO;
    }

    public void setMessengerServerFacade(MessengerServerFacade messengerServerFacade) {
        this.messengerServerFacade = messengerServerFacade;
    }

    @Override
    protected void run(CommandCallback<FlagMessageDTO> callback) throws Throwable {
        messengerServerFacade.getLoaderManager()
                .createFlaggingLoader()
                .flagMessage(flagMessageDTO)
                .subscribe(callback::onSuccess, callback::onFail);
    }

}
