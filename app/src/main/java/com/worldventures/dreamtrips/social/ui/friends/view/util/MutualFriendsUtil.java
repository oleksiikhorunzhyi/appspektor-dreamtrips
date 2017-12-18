package com.worldventures.dreamtrips.social.ui.friends.view.util;

import android.content.Context;
import android.text.TextUtils;

import com.worldventures.core.model.User;
import com.worldventures.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.R;

public class MutualFriendsUtil {

   private final Context context;

   public MutualFriendsUtil(Context context) {
      this.context = context;
   }

   public String createMutualString(User user) {
      String mutualString = null;
      if (hasMutualFriends(user)) {
         mutualString = context.getString(QuantityHelper.chooseResource(user.getMutualFriends()
               .getCount(), R.string.social_postfix_mutual_friends_one, R.string.social_postfix_mutual_friends), user.getMutualFriends()
               .getCount());
      }

      return mutualString;
   }

   public String createCircleAndMutualString(User user) {
      StringBuilder sb = new StringBuilder();
      if (!TextUtils.isEmpty(user.getCirclesString())) {
         sb.append(user.getCirclesString());

         if (hasMutualFriends(user)) {
            sb.append(", ");
         }
      }

      if (hasMutualFriends(user)) {
         sb.append(createMutualString(user));
      }

      return sb.toString();
   }

   public boolean hasMutualFriends(User user) {
      return user.getMutualFriends() != null && user.getMutualFriends().getCount() > 0;
   }
}
