package com.worldventures.dreamtrips.modules.auth.api.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.SessionAbsentException;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class UpdateAuthInfoCommand extends Command<Void> implements InjectableAction {

   @Inject SessionHolder<UserSession> appSessionHolder;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      Observable.just(isUserSessionTokenExist()).doOnNext(sessionExists -> {
         if (!sessionExists) throw new SessionAbsentException();
      }).subscribe(booleanObservable -> callback.onSuccess(null), callback::onFail);
   }

   private boolean isUserSessionTokenExist() {
      UserSession userSession = appSessionHolder.get().isPresent() ? appSessionHolder.get().get() : null;
      return userSession != null && userSession.getApiToken() != null;
   }
}
