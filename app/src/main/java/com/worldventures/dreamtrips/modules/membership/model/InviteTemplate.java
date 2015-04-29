package com.worldventures.dreamtrips.modules.membership.model;

import android.support.annotation.IntDef;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class InviteTemplate extends BaseEntity {
    public static final int EMAIL = 0;
    public static final int SMS = 1;


    String title;
    CoverImage coverImage;
    String video;
    String locale;
    String content;
    ArrayList<Member> to = new ArrayList<>(0);
    String from;
    @Type
    private int type;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    @IntDef({EMAIL, SMS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }
}
