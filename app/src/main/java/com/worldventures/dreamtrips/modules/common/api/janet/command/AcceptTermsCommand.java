package com.worldventures.dreamtrips.modules.common.api.janet.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.terms_and_conditions.AcceptTermsAndConditionsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class AcceptTermsCommand extends CommandWithError implements InjectableAction {

   @Inject Janet janet;
   @Inject SessionHolder<UserSession> appSessionHolder;

   private String text;

   public AcceptTermsCommand(String text) {
      this.text = text;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(AcceptTermsAndConditionsHttpAction.class)
            .createObservableResult(new AcceptTermsAndConditionsHttpAction(text))
            .doOnNext(action -> {
               UserSession userSession = appSessionHolder.get().get();
               userSession.getUser().setTermsAccepted(true);
               appSessionHolder.put(userSession);
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_accept_terms_and_conditions;
   }
}
