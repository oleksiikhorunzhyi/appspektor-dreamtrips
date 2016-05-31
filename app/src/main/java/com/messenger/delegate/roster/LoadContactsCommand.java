package com.messenger.delegate.roster;

import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LoadContactsCommand extends CommandActionBase<List<DataUser>> implements InjectableAction {

    @Inject MessengerServerFacade messengerServerFacade;
    @Inject Janet janet;
    @Inject UsersDAO usersDAO;

    @Override
    protected void run(CommandCallback<List<DataUser>> callback) throws Throwable {
        messengerServerFacade.getLoaderManager()
                .createContactLoader()
                .load()
                .doOnNext(contacts -> usersDAO.unfriendAll())
                .flatMap(users ->
                        janet.createPipe(FetchUsersDataCommand.class)
                                .createObservableSuccess(FetchUsersDataCommand.from(users))
                )
                .map(CommandActionBase::getResult)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
