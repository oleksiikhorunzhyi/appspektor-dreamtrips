package com.worldventures.dreamtrips.modules.friends.service.command;


import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.mappery.MapperyContext;

public abstract class GetUsersCommand extends CommandWithError<List<User>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   protected List<User> convert(Iterable<?> itemsToConvert) {
      return mapperyContext.convert(itemsToConvert, User.class);
   }
}
