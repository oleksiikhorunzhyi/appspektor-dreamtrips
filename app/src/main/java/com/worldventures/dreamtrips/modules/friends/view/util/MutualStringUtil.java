package com.worldventures.dreamtrips.modules.friends.view.util;

import android.content.Context;
import android.text.TextUtils;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;

public class MutualStringUtil {

    private Context context;

    public MutualStringUtil(Context context) {
        this.context = context;
    }

    public String createMutualString(User user) {
        String mutualString = null;
        if (user.getMutualFriends() > 0) {
            mutualString = context.getString(R.string.social_postfix_mutual_friends,
                    user.getMutualFriends());
        }

        return mutualString;
    }

    public String createCircleAndMutualString(User user) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(user.getCircles())) {
            sb.append(user.getCircles());

            if (user.getMutualFriends() > 0) {
                sb.append(", ");
            }
        }

        if (user.getMutualFriends() > 0) {
            sb.append(context.getString(R.string.social_postfix_mutual_friends, user.getMutualFriends()));
        }

        return sb.toString();
    }
}
