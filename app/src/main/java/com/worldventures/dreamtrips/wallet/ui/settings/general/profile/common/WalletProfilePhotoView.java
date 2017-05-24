package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;

import java.io.File;

import rx.Observable;

public interface WalletProfilePhotoView extends RxLifecycleView{

   void pickPhoto();

   void hidePhotoPicker();

   void cropPhoto(Uri photoPath);

   Observable<Uri> observePickPhoto();

   Observable<File> observeCropper();

   void setPreviewPhoto(@Nullable SmartCardUserPhoto photo);
}
