package com.worldventures.dreamtrips.modules.membership.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.util.ArrayList;

public class InviteTemplate extends BaseEntity {


    String title;
    CoverImage coverImage;
    String video;
    String locale;
    String content;
    ArrayList<Member> to = new ArrayList<>(0);
    String from;

    public ArrayList<Member> getTo() {
        return to;
    }

    public void setTo(ArrayList<Member> to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public CoverImage getCoverImage() {
        return coverImage;
    }

    public String getVideo() {
        return video;
    }

    public String getLocale() {
        return locale;
    }

    public String getContent() {
        return content;
    }
}
