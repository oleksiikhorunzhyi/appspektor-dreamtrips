package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcelable;

import java.io.Serializable;

public interface IFullScreenAvailableObject extends Serializable, Parcelable {

    public Image getFSImage();

    public String getFSTitle();

    public String getFsDescription();

    public String getFsShareText();

    public String getId();

    public String getUserName();

    public String getUserLocation();

    public String getUserAvatar();

    public String getPhotoLocation();

    int getFsCommentCount();


    int getFsLikeCount();

    String getFsLocation();

    String getFsDate();

    String getFsUserPhoto();
}
