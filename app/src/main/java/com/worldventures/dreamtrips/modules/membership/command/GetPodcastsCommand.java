package com.worldventures.dreamtrips.modules.membership.command;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.podcasts.GetPodcastsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.mapping.mapper.PodcastsMapper;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;


@CommandAction
public class GetPodcastsCommand extends CommandWithError<List<Podcast>> implements InjectableAction,
      CachedAction<List<Podcast>> {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject PodcastsMapper podcastsMapper;
   @Inject SnappyRepository db;

   private int page;
   private int perPage;

   private List<Podcast> cachedData;

   public GetPodcastsCommand(int page, int perPage) {
      this.page = page;
      this.perPage = perPage;
   }

   @Override
   protected void run(Command.CommandCallback<List<Podcast>> callback) throws Throwable {
      if (cachedData != null) {
         Observable.from(cachedData)
               .compose(new IoToMainComposer<>())
               .doOnNext(this::connectCachedEntity)
               .doOnNext(podcast -> callback.onProgress(0))
               .subscribe();
      }
      janet.createPipe(GetPodcastsHttpAction.class, Schedulers.io())
            .createObservableResult(new GetPodcastsHttpAction(page, perPage))
            .map(GetPodcastsHttpAction::response)
            .flatMap(Observable::from)
            .map(podcastsMapper::map)
            .doOnNext(this::connectCachedEntity)
            .toList()
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void connectCachedEntity(Podcast podcast) {
      podcast.setCacheEntity(db.getDownloadMediaEntity(podcast.getUid()));
   }

   @Override
   public List<Podcast> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<Podcast> cache) {
      cachedData = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder().build();
   }

   public List<Podcast> getItems() {
      List<Podcast> podcasts = new ArrayList<>();
      if (getResult() != null) {
         podcasts.addAll(getResult());
      } else if (cachedData != null) {
         podcasts.addAll(cachedData);
      }
      return podcasts;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_podcast;
   }
}
