package com.worldventures.dreamtrips.modules.auth.api.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

//TODO remove this command
@CommandAction
public class UpdateUserCommand extends Command<User> implements InjectableAction {

    private final User user;

    public UpdateUserCommand(User user) {
        this.user = user;
    }

    @Override
    protected void run(CommandCallback<User> callback) throws Throwable {
        Observable.just(user).subscribe(callback::onSuccess, callback::onFail);
    }
}
