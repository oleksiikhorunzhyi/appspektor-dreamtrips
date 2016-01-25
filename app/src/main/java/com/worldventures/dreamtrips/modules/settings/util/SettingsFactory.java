package com.worldventures.dreamtrips.modules.settings.util;

import com.worldventures.dreamtrips.modules.settings.model.FlagSettings;
import com.worldventures.dreamtrips.modules.settings.model.SelectSettings;
import com.worldventures.dreamtrips.modules.settings.model.Settings;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;

import java.util.ArrayList;
import java.util.List;

public class SettingsFactory {

    ////////////////////////////
    // Settings titles
    ////////////////////////////
    public static final String DISTANCE_UNITS = "distance_measurement_unit";
    //
    public static final String FRIEND_REQUEST = "receive_friend_request_notifications";
    public static final String NEW_POSTS = "receive_new_post_notifications";
    public static final String LIKES_AND_COMMENTS = "receive_likes_and_comments_notifications";
    public static final String NEW_MESSAGE = "receive_new_message_notifications";
    public static final String PHOTO_TAGGING = "receive_tagged_on_photo_notifications";

    ////////////////////////////
    // Settings select options
    ////////////////////////////
    public static final String MILES = "miles";
    public static final String KILOMETERS = "kilometers";

    public List<Settings> createSettings(SettingsGroup group) {
        switch (group.getType()) {
            case GENERAL:
                return createGeneralSettings();
            case NOTIFICATIONS:
                return createNotificationSettings();
            default:
                return new ArrayList<>();
        }
    }

    private List<Settings> createGeneralSettings() {
        List<Settings> settingsList = new ArrayList<>();
        List<String> options = new ArrayList<>();
        options.add(MILES);
        options.add(KILOMETERS);
        settingsList.add(new SelectSettings(DISTANCE_UNITS, Settings.Type.SELECT, MILES, options));

        return settingsList;
    }

    private List<Settings> createNotificationSettings() {
        List<Settings> settingsList = new ArrayList<>();
        settingsList.add(new FlagSettings(FRIEND_REQUEST, Settings.Type.FLAG, true));
        settingsList.add(new FlagSettings(NEW_POSTS, Settings.Type.FLAG, true));
        settingsList.add(new FlagSettings(LIKES_AND_COMMENTS, Settings.Type.FLAG, true));
        settingsList.add(new FlagSettings(NEW_MESSAGE, Settings.Type.FLAG, true));
        settingsList.add(new FlagSettings(PHOTO_TAGGING, Settings.Type.FLAG, true));

        return settingsList;
    }
}
