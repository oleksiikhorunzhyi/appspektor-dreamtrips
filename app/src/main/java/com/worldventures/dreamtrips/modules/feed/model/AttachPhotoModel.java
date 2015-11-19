package com.worldventures.dreamtrips.modules.feed.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AttachPhotoModel {

    public static final int CAMERA = 1;
    public static final int FACEBOOK = 2;

    private int iconRes;
    private int titleRes;
    private int colorRes;

    private int attachType;

    public AttachPhotoModel(@AttachType int attachType, int iconRes, int titleRes, int colorRes) {
        this.attachType = attachType;
        this.iconRes = iconRes;
        this.titleRes = titleRes;
        this.colorRes = colorRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getTitleRes() {
        return titleRes;
    }

    public int getColorRes() {
        return colorRes;
    }

    @AttachType
    public int getType() {
        return attachType;
    }

    @IntDef({CAMERA, FACEBOOK})
    @Retention(RetentionPolicy.SOURCE)
    @interface AttachType {
    }
}