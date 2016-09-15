package com.worldventures.dreamtrips.modules.profile.view;

import android.content.res.Resources;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;

public class ProfileViewUtils {


   public static void setUserStatus(User user, TextView userStatus, Resources resources) {
      if (user.isGold()) ProfileViewUtils.setGold(userStatus, resources);
      else if (user.isPlatinum()) ProfileViewUtils.setPlatinum(userStatus, resources);
      else ProfileViewUtils.setMember(userStatus, resources);
   }

   private static void setMember(TextView tv, Resources res) {
      setUI(tv, R.string.empty, 0, res.getColor(R.color.white));
   }

   private static void setGold(TextView tv, Resources res) {
      setUI(tv, R.string.profile_golden, R.drawable.ic_profile_gold_member, res.getColor(R.color.golden_user));
   }

   private static void setPlatinum(TextView tv, Resources res) {
      setUI(tv, R.string.profile_platinum, R.drawable.ic_profile_platinum_member, res.getColor(R.color.platinum_user));
   }


   private static void setUI(TextView textView, int title, int drawable, int color) {
      textView.setTextColor(color);
      textView.setText(title);
      textView.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
   }


}
