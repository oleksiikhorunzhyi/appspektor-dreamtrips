package com.worldventures.dreamtrips.modules.friends.service.command;


import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import javax.inject.Inject;

import io.techery.mappery.MapperyContext;

public abstract class GetUsersCommand extends CommandWithError<List<User>> implements InjectableAction{

   @Inject MapperyContext mapperyContext;

   protected List<User> convert(Iterable<?> itemsToConvert) {
      return mapperyContext.convert(itemsToConvert, User.class);
   }
}
