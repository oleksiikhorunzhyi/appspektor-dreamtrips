package com.worldventures.core.modules.facebook.service.command;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.reflect.TypeToken;
import com.worldventures.core.R;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.CacheBundleImpl;
import com.worldventures.core.janet.cache.CacheOptions;
import com.worldventures.core.janet.cache.CachedAction;
import com.worldventures.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.core.janet.cache.storage.PaginatedStorage;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.core.modules.facebook.FacebookHelper;
import com.worldventures.core.modules.facebook.model.FacebookAlbum;
import com.worldventures.core.modules.facebook.model.FacebookAlbumsGraph;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

import static com.facebook.HttpMethod.GET;

@CommandAction
public class GetAlbumsCommand extends CommandWithError<List<FacebookAlbum>> implements InjectableAction,
      CachedAction<FacebookAlbumsGraph> {

   private static final String ROUTE = "/me/albums?fields=id,name,count,cover_photo";

   @Inject FacebookHelper facebookHelper;

   private GraphResponse cachedGraphResponse;
   private GraphResponse graphResponse;
   private boolean refresh;

   public GetAlbumsCommand(boolean refresh) {
      this.refresh = refresh;
   }

   @Override
   protected void run(CommandCallback<List<FacebookAlbum>> callback) throws Throwable {
      GraphRequest graphRequest;
      if (refresh) {
         graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), ROUTE, null, GET);
      } else {
         graphRequest = cachedGraphResponse.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
         // null is returned by Facebook SDK if no pagination is available
         if (graphRequest == null) {
            graphResponse = cachedGraphResponse;
            callback.onSuccess(new ArrayList<>());
            return;
         }
      }
      graphResponse = graphRequest.executeAndWait();
      if (graphResponse.getError() == null) {
         callback.onSuccess(facebookHelper.processList(graphResponse, new TypeToken<List<FacebookAlbum>>() {}));
      } else {
         callback.onFail(facebookHelper.getThrowableFromGraphError(graphResponse.getError()));
      }
   }

   @Override
   public FacebookAlbumsGraph getCacheData() {
      return new FacebookAlbumsGraph(graphResponse);
   }

   @Override
   public void onRestore(ActionHolder holder, FacebookAlbumsGraph cache) {
      cachedGraphResponse = cache.getGraphResponse();
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, refresh);
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.facebook_error_loading_albums;
   }

   public static GetAlbumsCommand refresh() {
      return new GetAlbumsCommand(true);
   }

   public static GetAlbumsCommand loadMore() {
      return new GetAlbumsCommand(false);
   }
}
