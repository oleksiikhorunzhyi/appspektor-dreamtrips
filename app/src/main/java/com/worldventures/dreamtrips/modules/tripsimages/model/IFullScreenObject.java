package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.Serializable;

public interface IFullScreenObject extends Serializable, Parcelable {

    Image getFSImage();

    String getFSTitle();

    String getFsDescription();

    String getFsShareText();

    String getFsId();

    String getPhotoLocation();

    int getFsCommentCount();

    int getFsLikeCount();

    String getFsLocation();

    String getFsDate();

    String getFsUserPhoto();

    User getUser();
}
