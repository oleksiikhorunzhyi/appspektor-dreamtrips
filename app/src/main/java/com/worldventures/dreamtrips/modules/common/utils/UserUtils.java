package com.worldventures.dreamtrips.modules.common.utils;


import android.content.Context;
import android.text.Spanned;
import android.text.TextUtils;

import com.worldventures.core.model.User;
import com.worldventures.core.utils.ProjectTextUtils;
import com.worldventures.dreamtrips.R;

public final class UserUtils {

   private UserUtils() {
   }

   public static Spanned getUsernameWithCompany(Context context, User user) {
      String userWithCompany = !TextUtils.isEmpty(user.getCompany()) ? context.getString(R.string.user_name_with_company, user
            .getFullName(), user.getCompany()) : context
            .getString(R.string.user_name, user.getFullName());
      return ProjectTextUtils.fromHtml(userWithCompany);
   }
}
