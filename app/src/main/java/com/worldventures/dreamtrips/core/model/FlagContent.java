package com.worldventures.dreamtrips.core.model;

public enum FlagContent {
    INDECENT_CONTENT("Indecent Content", true),
    VIOLENT_CONTENT("Violent Content", false),
    CULTURALLY_INSENSITIVE("Culturally Insensitive", true),
    OTHER("OTHER", true);

    FlagContent(String title, boolean needDescription) {
        this.title = title;
        this.needDescription = needDescription;
    }

    String title;
    boolean needDescription;

    public String getTitle() {
        return title;
    }

    public boolean isNeedDescription() {
        return needDescription;
    }
}
