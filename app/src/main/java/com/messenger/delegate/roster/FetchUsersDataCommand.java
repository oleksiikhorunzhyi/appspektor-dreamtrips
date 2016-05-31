package com.messenger.delegate.roster;

import com.messenger.delegate.UserProcessor;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.MessengerUser;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.CommandActionBase;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchUsersDataCommand extends CommandActionBase<List<DataUser>> implements InjectableAction {

    @Inject UserProcessor userProcessor;

    private List<MessengerUser> messengerUserList;

    private FetchUsersDataCommand(List<MessengerUser> messengerUserList) {
        this.messengerUserList = messengerUserList;
    }

    public static FetchUsersDataCommand from(List<MessengerUser> users) {
        return new FetchUsersDataCommand(users);
    }

    @Override
    protected void run(CommandCallback<List<DataUser>> callback) throws Throwable {
        userProcessor.syncUsers(messengerUserList)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
