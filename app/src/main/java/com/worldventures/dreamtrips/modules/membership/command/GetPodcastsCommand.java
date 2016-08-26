package com.worldventures.dreamtrips.modules.membership.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.podcasts.GetPodcastsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.mapping.mapper.PodcastsMapper;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;


@CommandAction
public class GetPodcastsCommand extends CommandWithError<List<Podcast>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject PodcastsMapper podcastsMapper;

   private int page;
   private int perPage;

   public GetPodcastsCommand(int page, int perPage) {
      this.page = page;
      this.perPage = perPage;
   }

   @Override
   protected void run(Command.CommandCallback<List<Podcast>> callback) throws Throwable {
      janet.createPipe(GetPodcastsHttpAction.class, Schedulers.io())
            .createObservableResult(new GetPodcastsHttpAction(page, perPage))
            .map(GetPodcastsHttpAction::response)
            .flatMap(Observable::from)
            .map(podcastsMapper::map)
            .toList()
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_podcast;
   }
}
