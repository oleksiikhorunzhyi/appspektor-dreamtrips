package com.worldventures.dreamtrips.modules.membership.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.membership.api.GetPodcastsHttpAction;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;


@CommandAction
public class PodcastCommand extends CommandWithError<List<Podcast>> implements InjectableAction {

   @Inject Janet janet;

   private int page;
   private int perPage;

   public PodcastCommand(int page, int perPage) {
      this.page = page;
      this.perPage = perPage;
   }

   @Override
   protected void run(Command.CommandCallback<List<Podcast>> callback) throws Throwable {
      janet.createPipe(GetPodcastsHttpAction.class, Schedulers.io())
            .createObservableResult(new GetPodcastsHttpAction(page, perPage))
            .map(GetPodcastsHttpAction::getResponseItems)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_podcast;
   }
}
