package com.worldventures.dreamtrips.modules.membership.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

public class InviteTemplate extends BaseEntity {

    String title;
    CoverImage coverImage;
    String video;
    String locale;
    String content;

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
