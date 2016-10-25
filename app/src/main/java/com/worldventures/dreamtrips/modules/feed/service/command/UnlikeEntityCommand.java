package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.likes.DislikeHttpAction;
import com.worldventures.dreamtrips.api.likes.LikeHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class UnlikeEntityCommand extends CommandWithError implements InjectableAction {

   private String uid;

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   public UnlikeEntityCommand(String uid) {
      this.uid = uid;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(DislikeHttpAction.class)
            .createObservableResult(new DislikeHttpAction(uid))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_unlike_item;
   }
}