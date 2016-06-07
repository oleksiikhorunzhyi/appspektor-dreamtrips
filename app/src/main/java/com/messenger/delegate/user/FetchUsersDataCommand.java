package com.messenger.delegate.user;

import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.MessengerUser;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class FetchUsersDataCommand extends Command<List<DataUser>> implements InjectableAction {

    @Inject UserDataFetcher userDataFetcher;

    private List<MessengerUser> messengerUserList;

    private FetchUsersDataCommand(List<MessengerUser> messengerUserList) {
        this.messengerUserList = messengerUserList;
    }

    public static FetchUsersDataCommand from(List<MessengerUser> users) {
        return new FetchUsersDataCommand(users);
    }

    @Override
    protected void run(CommandCallback<List<DataUser>> callback) throws Throwable {
        userDataFetcher.fetchUserData(messengerUserList)
                .subscribe(callback::onSuccess, callback::onFail);
    }
}
