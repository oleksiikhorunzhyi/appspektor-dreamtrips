package com.worldventures.dreamtrips.social.ui.friends.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.model.User;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.mappery.MapperyContext;

public abstract class GetUsersCommand extends CommandWithError<List<User>> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   protected List<User> convert(Iterable<?> itemsToConvert) {
      return mapperyContext.convert(itemsToConvert, User.class);
   }
}
