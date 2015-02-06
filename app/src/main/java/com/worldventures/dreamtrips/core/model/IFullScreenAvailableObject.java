package com.worldventures.dreamtrips.core.model;

import java.io.Serializable;

public interface IFullScreenAvailableObject extends Serializable{

    public Image getFSImage();
    public String  getFSTitle();
    public String getFsDescription();
    public String getFsShareText();

}
