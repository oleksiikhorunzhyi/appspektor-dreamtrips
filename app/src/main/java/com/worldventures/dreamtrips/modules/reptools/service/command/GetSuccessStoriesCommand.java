package com.worldventures.dreamtrips.modules.reptools.service.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.success_stories.GetSuccessStoriesHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetSuccessStoriesCommand extends CommandWithError<List<SuccessStory>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mappery;

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(GetSuccessStoriesHttpAction.class)
            .createObservableResult(new GetSuccessStoriesHttpAction())
            .map(action -> mappery.convert(action.response(), SuccessStory.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_success_stories;
   }
}
