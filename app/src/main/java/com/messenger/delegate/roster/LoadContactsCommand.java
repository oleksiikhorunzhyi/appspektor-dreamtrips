package com.messenger.delegate.roster;

import com.messenger.delegate.user.UsersDelegate;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.storage.dao.UsersDAO;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LoadContactsCommand extends CommandActionBase<List<DataUser>> implements InjectableAction {

    @Inject MessengerServerFacade messengerServerFacade;
    @Inject UsersDelegate usersDelegate;
    @Inject UsersDAO usersDAO;

    @Override
    protected void run(CommandCallback<List<DataUser>> callback) throws Throwable {
        messengerServerFacade.getLoaderManager()
                .createContactLoader()
                .load()
                .flatMap(users ->
                        usersDelegate.loadUsers(users)
                                .doOnNext(action -> usersDAO.unfriendAll())
                                .doOnNext(dataUsers -> usersDAO.save(dataUsers))
                )
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
