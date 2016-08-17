package com.worldventures.dreamtrips.modules.profile.api;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;

public class GetProfileQuery extends Query<User> {

   SessionHolder<UserSession> appSessionHolder;

   public GetProfileQuery(SessionHolder<UserSession> sessionHolder) {
      super(User.class);
      this.appSessionHolder = sessionHolder;
   }

   @Override
   public User loadDataFromNetwork() throws Exception {
      User account = getService().getProfile();
      UserSession userSession = appSessionHolder.get().get();
      userSession.setUser(account);
      appSessionHolder.put(userSession);
      return account;
   }

   @Override
   public int getErrorMessage() {
      return R.string.error_fail_to_load_profile_info;
   }
}
