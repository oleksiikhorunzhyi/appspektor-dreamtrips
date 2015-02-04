package com.worldventures.dreamtrips.view.fragment.navigationdrawer;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class NavigationHeader {
    Uri userCover;
    Uri userPhoto;
    Drawable userPhoto2;
    Drawable userPhoto3;
    String userNome;
    String userEmail;

    public Uri getUserCover() {
        return userCover;
    }

    public void setUserCover(Uri userCover) {
        this.userCover = userCover;
    }

    public Uri getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(Uri userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Drawable getUserPhoto2() {
        return userPhoto2;
    }

    public void setUserPhoto2(Drawable userPhoto2) {
        this.userPhoto2 = userPhoto2;
    }

    public Drawable getUserPhoto3() {
        return userPhoto3;
    }

    public void setUserPhoto3(Drawable userPhoto3) {
        this.userPhoto3 = userPhoto3;
    }

    public String getUserNome() {
        return userNome;
    }

    public void setUserNome(String userNome) {
        this.userNome = userNome;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
