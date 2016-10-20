package com.worldventures.dreamtrips.modules.friends.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.likes.GetLikersHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetLikersCommand extends GetUsersCommand {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   private String uid;
   private int page;
   private int perPage;

   public GetLikersCommand(String uid, int page, int perPage) {
      this.uid = uid;
      this.page = page;
      this.perPage = perPage;
   }

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(GetLikersHttpAction.class)
            .createObservableResult(new GetLikersHttpAction(uid, page, perPage))
            .map(GetLikersHttpAction::response)
            .map(this::convert)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_people_who_liked;
   }
}
