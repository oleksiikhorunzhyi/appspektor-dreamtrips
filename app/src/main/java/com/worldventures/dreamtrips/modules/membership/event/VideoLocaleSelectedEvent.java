package com.worldventures.dreamtrips.modules.membership.event;

import com.worldventures.dreamtrips.modules.reptools.model.VideoLocale;

public class VideoLocaleSelectedEvent {

    VideoLocale videoLocale;

    public VideoLocaleSelectedEvent(VideoLocale videoLocale) {
        this.videoLocale = videoLocale;
    }

    public VideoLocale getVideoLocale() {
        return videoLocale;
    }
}
