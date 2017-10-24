package com.worldventures.dreamtrips.wallet.service;


import android.content.Intent;
import android.net.Uri;

import java.io.File;

import rx.Observable;

public interface WalletCropImageService {
   String SERVICE_NAME = WalletCropImageService.class.getName();

   Observable<File> observeCropper();

   void cropImage(Uri uri);

   void destroy();

   boolean onActivityResult(int requestCode, int resultCode, Intent data);
}
