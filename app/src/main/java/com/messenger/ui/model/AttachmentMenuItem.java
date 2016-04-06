package com.messenger.ui.model;

import android.support.annotation.StringDef;

import com.messenger.ui.presenter.ChatScreenPresenter;

public class AttachmentMenuItem {
    public static final String LOCATION = "location";
    public static final String IMAGE = "image";
    public static final String CANCEL = "cancel";

    @StringDef({LOCATION, IMAGE, CANCEL})
    public @interface AttachmentType {
    }

    private String title;
    private String type;

    public AttachmentMenuItem(@AttachmentType String type, String title) {
        this.title = title;
        this.type = type;
    }

    @AttachmentType
    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
