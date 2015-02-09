package com.worldventures.dreamtrips.core.model;

import android.os.Parcelable;

import java.io.Serializable;

public interface IFullScreenAvailableObject extends Serializable, Parcelable{

    public Image getFSImage();

    public String getFSTitle();

    public String getFsDescription();

    public String getFsShareText();

    public String getId();

}
