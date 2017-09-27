package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.net.Uri;

import java.io.File;

import rx.Observable;

public interface WalletProfilePhotoView {

   void pickPhoto(String initialPhotoUrl);

   void cropPhoto(Uri photoPath);

   Observable<File> observeCropper();

   void dropPhoto();

   void showDialog();

   void hideDialog();
}
