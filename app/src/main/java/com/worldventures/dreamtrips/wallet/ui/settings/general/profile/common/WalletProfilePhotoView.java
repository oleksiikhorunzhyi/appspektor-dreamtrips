package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.net.Uri;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;

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
