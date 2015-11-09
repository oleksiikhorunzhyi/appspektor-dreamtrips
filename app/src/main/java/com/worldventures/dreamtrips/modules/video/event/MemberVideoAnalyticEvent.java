package com.worldventures.dreamtrips.modules.video.event;


public class MemberVideoAnalyticEvent {

    String videoName;
    String actionAttribute;

    public MemberVideoAnalyticEvent(String videoName, String actionAttribute) {
        this.videoName = videoName;
        this.actionAttribute = actionAttribute;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getActionAttribute() {
        return actionAttribute;
    }
}
