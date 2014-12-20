package com.worldventures.dreamtrips.view.fragment.navigationdrawer;

import android.graphics.drawable.Drawable;

public class NavigationHeader {
    Drawable userCover;
    Drawable userPhoto;
    Drawable userPhoto2;
    Drawable userPhoto3;
    String userNome;
    String userEmail;

    public Drawable getUserCover() {
        return userCover;
    }

    public void setUserCover(Drawable userCover) {
        this.userCover = userCover;
    }

    public Drawable getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(Drawable userPhoto) {
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
