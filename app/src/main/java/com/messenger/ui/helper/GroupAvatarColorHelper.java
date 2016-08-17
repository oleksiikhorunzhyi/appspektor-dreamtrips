package com.messenger.ui.helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;

public class GroupAvatarColorHelper {

   private static int[] COLORS_IDS = {R.color.group_avatar_color_0, R.color.group_avatar_color_1, R.color.group_avatar_color_2, R.color.group_avatar_color_3, R.color.group_avatar_color_4, R.color.group_avatar_color_5, R.color.group_avatar_color_6, R.color.group_avatar_color_7, R.color.group_avatar_color_8, R.color.group_avatar_color_9, R.color.group_avatar_color_10, R.color.group_avatar_color_11, R.color.group_avatar_color_12, R.color.group_avatar_color_13, R.color.group_avatar_color_14, R.color.group_avatar_color_15,};

   public int obtainColor(Context context, String groupConversationId) {
      int sum = Queryable.from(groupConversationId.toCharArray()).map(c -> (int) c).sum();
      return ContextCompat.getColor(context, COLORS_IDS[sum % COLORS_IDS.length]);
   }
}
