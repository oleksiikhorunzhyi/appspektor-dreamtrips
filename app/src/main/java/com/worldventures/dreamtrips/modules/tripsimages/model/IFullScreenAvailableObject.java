package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcelable;

import java.io.Serializable;

public interface IFullScreenAvailableObject extends Serializable, Parcelable {

    Image getFSImage();

    String getFSTitle();

    String getFsDescription();

    String getFsShareText();

    String getId();

    String getUserName();

    String getUserLocation();

    String getUserAvatar();

    String getPhotoLocation();

    int getFsCommentCount();

    int getFsLikeCount();

    String getFsLocation();

    String getFsDate();

    String getFsUserPhoto();
}
