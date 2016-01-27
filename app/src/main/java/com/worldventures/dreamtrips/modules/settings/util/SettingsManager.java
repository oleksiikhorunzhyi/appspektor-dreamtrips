package com.worldventures.dreamtrips.modules.settings.util;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.settings.model.Settings;

import java.util.List;

import static com.worldventures.dreamtrips.modules.settings.util.SettingsFactory.*;

public class SettingsManager {

    public List<Settings> merge(List<Settings> fromServer, List<Settings> local) {
        return Queryable.from(fromServer).filter(local::contains).map(setting -> {
            Settings localSetting = Queryable.from(local).firstOrDefault(setting::equals);
            if (localSetting != null) setting.setType(localSetting.getType());
            return setting;
        }).toList();
    }

    public int getLocalizedTitleResource(String title) {
        switch (title) {
            case DISTANCE_UNITS:
                return R.string.settings_distance_units;
            case FRIEND_REQUEST:
                return R.string.settings_friend_requests;
            case NEW_POSTS:
                return R.string.settings_new_posts;
            case LIKES_AND_COMMENTS:
                return R.string.settings_likes_and_comments;
            case NEW_MESSAGE:
                return R.string.settings_new_message;
            case PHOTO_TAGGING:
                return R.string.settings_photo_tagging;
            default:
                return 0;
        }
    }

    public int getLocalizedOptionResource(String option) {
        switch (option) {
            case MILES:
                return R.string.abbreviated_miles;
            case KILOMETERS:
                return R.string.abbreviated_kilometers;
            default:
                return 0;
        }
    }
}
