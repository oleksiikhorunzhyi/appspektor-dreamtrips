package com.worldventures.dreamtrips.modules.friends.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.friends.GetFriendRequestsHttpAction;
import com.worldventures.dreamtrips.api.friends.model.ImmutableGetFriendRequestsParams;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CacheBundleImpl;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.storage.PaginatedStorage;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.ActionHolder;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class GetRequestsCommand extends GetUsersCommand implements CachedAction<List<User>> {
   private static final int PER_PAGE = 100;

   private List<User> cachedUsers;

   private int page;

   public GetRequestsCommand(int page) {
      this.page = page;
   }

   @Override
   protected void run(CommandCallback<List<User>> callback) throws Throwable {
      janet.createPipe(GetFriendRequestsHttpAction.class)
            .createObservableResult(new GetFriendRequestsHttpAction(ImmutableGetFriendRequestsParams.builder()
                  .page(page)
                  .perPage(PER_PAGE)
                  .build()))
            .map(GetFriendRequestsHttpAction::response)
            .map(this::convert)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_failed_to_load_friend_requests;
   }

   @Override
   public List<User> getCacheData() {
      return new ArrayList<>(getResult());
   }

   @Override
   public void onRestore(ActionHolder holder, List<User> cache) {
      cachedUsers = new ArrayList<>(cache);
   }

   @Override
   public CacheOptions getCacheOptions() {
      CacheBundle cacheBundle = new CacheBundleImpl();
      cacheBundle.put(PaginatedStorage.BUNDLE_REFRESH, isFirstPage());
      return ImmutableCacheOptions.builder().params(cacheBundle).build();
   }

   public List<User> items() {
      List<User> users = new ArrayList<>();

      //we should add previous loaded pages in beginning of list
      //and avoid situation when we add entire cache of previous loading session
      if (getResult() != null) {
         if (!isFirstPage() && cachedUsers != null) users.addAll(cachedUsers);
         users.addAll(getResult());
      } else if (cachedUsers != null) users.addAll(cachedUsers);

      return users;
   }

   public boolean isFirstPage() {
      return page == 1;
   }

   public boolean isNoMoreElements() {
      //due to details of backend implementation, we assume that there are no more items, if result is empty
      return getResult().size() == 0;
   }
}


