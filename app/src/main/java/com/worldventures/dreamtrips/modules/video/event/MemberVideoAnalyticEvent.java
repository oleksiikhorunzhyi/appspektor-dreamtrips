package com.worldventures.dreamtrips.modules.video.event;


public class MemberVideoAnalyticEvent {

    String videoId;
    String actionAttribute;

    public MemberVideoAnalyticEvent(String videoId, String actionAttribute) {
        this.videoId = videoId;
        this.actionAttribute = actionAttribute;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getActionAttribute() {
        return actionAttribute;
    }
}
