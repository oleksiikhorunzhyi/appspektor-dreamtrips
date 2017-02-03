package com.worldventures.dreamtrips.modules.facebook.service.command;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.gson.reflect.TypeToken;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedStorage;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.facebook.FacebookHelper;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhotosGraph;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

import static com.facebook.HttpMethod.GET;

@CommandAction
public class GetPhotosCommand extends CommandWithError<List<FacebookPhoto>> implements InjectableAction,
      CachedAction<FacebookPhotosGraph> {

   private static final String ROUTE = "/{album-id}/photos?fields=id,images";

   @Inject FacebookHelper facebookHelper;

   private GraphResponse cachedGraphResponse;
   private GraphResponse graphResponse;
   private boolean refresh;

   private String albumId;

   public GetPhotosCommand(String albumId, boolean refresh) {
      this.albumId = albumId;
      this.refresh = refresh;
   }

   @Override
   protected void run(CommandCallback<List<FacebookPhoto>> callback) throws Throwable {
      GraphRequest graphRequest;
      if (refresh) {
         graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), getRoute(), null, GET);
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
      callback.onSuccess(facebookHelper.processList(graphResponse, new TypeToken<List<FacebookPhoto>>(){}));
   }

   private String getRoute() {
      return ROUTE.replace("{album-id}", albumId);
   }

   @Override
   public FacebookPhotosGraph getCacheData() {
      return new FacebookPhotosGraph(graphResponse);
   }

   @Override
   public void onRestore(ActionHolder holder, FacebookPhotosGraph cache) {
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
      return R.string.facebook_error_loading_photos;
   }

   public static GetPhotosCommand refresh(String albumId) {
      return new GetPhotosCommand(albumId, true);
   }

   public static GetPhotosCommand loadMore(String albumId) {
      return new GetPhotosCommand(albumId, false);
   }
}
