package com.worldventures.dreamtrips.modules.friends.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;

public class GetFriendsQuery extends Query<ArrayList<User>> {

   private Circle circle;
   private String query;
   private int page;
   private int perPage;

   public GetFriendsQuery(Circle circle, String query, int page, int perPage) {
      super((Class<ArrayList<User>>) new ArrayList<User>().getClass());
      this.circle = circle;
      this.perPage = perPage;
      this.page = page;
      this.query = query != null && query.length() > 2 ? query : null;
   }

   @Override
   public ArrayList<User> loadDataFromNetwork() throws Exception {
      if (circle != null) return getService().getFriends(circle.getId(), query, page, perPage);
      else return getService().getAllFriends(query, page, perPage);
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_failed_to_load_friends;
   }
}
