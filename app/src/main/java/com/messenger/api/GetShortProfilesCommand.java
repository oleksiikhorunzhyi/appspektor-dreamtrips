package com.messenger.api;


import com.worldventures.dreamtrips.api.messenger.GetShortProfileHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@CommandAction
public class GetShortProfilesCommand extends Command<List<User>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   List<String> userNames;

   public GetShortProfilesCommand(List<String> userNames) {
      this.userNames = userNames;
   }

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(GetShortProfileHttpAction.class)
            .createObservableResult(new GetShortProfileHttpAction(userNames))
            .map(GetShortProfileHttpAction::getShortUsers)
            .flatMap(Observable::from)
            .map(items -> mappery.convert(items, User.class))
            .toList()
            .subscribe(callback::onSuccess, callback::onFail);
   }
}