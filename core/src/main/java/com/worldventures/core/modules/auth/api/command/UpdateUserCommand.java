package com.worldventures.core.modules.auth.api.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.model.User;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@Deprecated
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
