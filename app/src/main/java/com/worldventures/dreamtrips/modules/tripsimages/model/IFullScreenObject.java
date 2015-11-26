package com.worldventures.dreamtrips.modules.tripsimages.model;

import android.os.Parcelable;

import com.worldventures.dreamtrips.core.ui.fragment.ImagePathHolder;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.io.Serializable;

public interface IFullScreenObject extends ImagePathHolder, Serializable, Parcelable {

    Image getFSImage();

    String getFSTitle();

    String getFsDescription();

    String getFsShareText();

    String getFsId();

    int getFsCommentCount();

    int getFsLikeCount();

    String getFsLocation();

    String getFsDate();

    String getFsUserPhoto();

    User getUser();
}
