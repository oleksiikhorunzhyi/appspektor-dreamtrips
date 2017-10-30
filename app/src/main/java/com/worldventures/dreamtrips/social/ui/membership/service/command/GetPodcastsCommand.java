package com.worldventures.dreamtrips.social.ui.membership.service.command;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CacheBundleImpl;
import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.core.janet.cache.storage.PaginatedStorage;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.modules.video.service.storage.MediaModelStorage;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.podcasts.GetPodcastsHttpAction;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.social.domain.mapping.PodcastsMapper;
import com.worldventures.dreamtrips.social.ui.membership.model.Podcast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public final class GetPodcastsCommand extends CommandWithError<List<Podcast>> implements InjectableAction,
      CachedAction<List<Podcast>> {

   public static final int PAGE_SIZE = 10;

   @Inject Janet janet;
   @Inject PodcastsMapper podcastsMapper;
   @Inject MediaModelStorage db;

   private boolean refresh;

   private List<Podcast> cachedData;

   private GetPodcastsCommand(boolean refresh) {
      this.refresh = refresh;
   }

   @Override
   protected void run(Command.CommandCallback<List<Podcast>> callback) throws Throwable {
      if (cachedData != null && !cachedData.isEmpty()) {
         Observable.from(cachedData)
               .compose(new IoToMainComposer<>())
               .doOnNext(this::connectCachedEntity)
               .toList()
               .doOnNext(podcasts -> callback.onProgress(0))
               .subscribe();
      }
      janet.createPipe(GetPodcastsHttpAction.class, Schedulers.io())
            .createObservableResult(new GetPodcastsHttpAction(getPage(), PAGE_SIZE))
            .map(GetPodcastsHttpAction::response)
            .flatMap(Observable::from)
            .map(podcastsMapper::map)
            .doOnNext(this::connectCachedEntity)
            .toList()
            .doOnNext(podcasts -> clearCacheIfNeeded())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void connectCachedEntity(Podcast podcast) {
      podcast.setCacheEntity(db.getDownloadMediaModel(podcast.getUid()));
   }

   private void clearCacheIfNeeded() {
      if (refresh) {
         cachedData = null;
      }
   }

   private int getPage() {
      if (refresh || cachedData == null || cachedData.isEmpty()) {
         return 1;
      }
      return cachedData.size() / PAGE_SIZE + 1;
   }

   @Override
   public List<Podcast> getCacheData() {
      return new ArrayList<>(getResult());
   }

   public boolean hasMore() {
      return getResult().size() == PAGE_SIZE;
   }

   @Override
   public void onRestore(ActionHolder holder, List<Podcast> cache) {
      cachedData = new ArrayList<>(cache);
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, refresh);
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }

   public List<Podcast> getItems() {
      List<Podcast> podcasts = new ArrayList<>();
      if (cachedData != null) {
         podcasts.addAll(cachedData);
      }
      if (getResult() != null) {
         podcasts.addAll(getResult());
      }
      return podcasts;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_podcast;
   }

   public static GetPodcastsCommand refresh() {
      return new GetPodcastsCommand(true);
   }

   public static GetPodcastsCommand loadMore() {
      return new GetPodcastsCommand(false);
   }
}
