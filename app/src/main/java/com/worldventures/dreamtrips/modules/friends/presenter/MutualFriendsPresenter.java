package com.worldventures.dreamtrips.modules.friends.presenter;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.api.GetMutualFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.bundle.MutualFriendsBundle;

import java.util.ArrayList;

public class MutualFriendsPresenter extends BaseUserListPresenter<MutualFriendsPresenter.View> {

   private int userId;

   public MutualFriendsPresenter(MutualFriendsBundle bundle) {
      userId = bundle.getId();
   }

   @Override
   protected Query<ArrayList<User>> getUserListQuery(int page) {
      return new GetMutualFriendsQuery(userId);
   }

   @Override
   public void scrolled(int totalItemCount, int lastVisible) {
      // now api doesn't have pagination therefore lazy loading is disabled
   }

   @Override
   protected void userStateChanged(User user) {
      view.finishLoading();

      int index = users.indexOf(user);
      if (index != -1) {
         users.remove(index);
         users.add(index, user);
         view.refreshUsers(users);
      }
   }

   public interface View extends BaseUserListPresenter.View {

   }
}
