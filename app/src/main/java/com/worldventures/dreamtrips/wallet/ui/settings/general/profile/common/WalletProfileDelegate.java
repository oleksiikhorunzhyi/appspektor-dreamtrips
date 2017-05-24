package com.worldventures.dreamtrips.wallet.ui.settings.general.profile.common;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;

public class WalletProfileDelegate {

   private AnalyticsInteractor analyticsInteractor;

   @Nullable private SmartCardUserPhoto preparedPhoto;

   public WalletProfileDelegate(
         AnalyticsInteractor analyticsInteractor) {
      this.analyticsInteractor = analyticsInteractor;
   }

   public void setupInputMode(Activity activity) {
      activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
   }

   public void sendAnalytics(WalletAnalyticsAction action) {
      final WalletAnalyticsCommand analyticsCommand = new WalletAnalyticsCommand(action);
      analyticsInteractor.walletAnalyticsCommandPipe().send(analyticsCommand);
   }

   public void observePickerAndCropper(WalletProfilePhotoView view) {
      view.observePickPhoto().compose(view.lifecycle()).subscribe(view::cropPhoto);
      view.observeCropper().compose(view.lifecycle()).subscribe(photoFile -> setPhotoUri(Uri.fromFile(photoFile).toString(), view));
   }

   public void setPhotoUri(String photoUri, WalletProfilePhotoView view) {
      preparedPhoto = SmartCardUserPhoto.of(photoUri);
      view.setPreviewPhoto(preparedPhoto);
   }

   @Nullable
   public SmartCardUserPhoto preparedPhoto() {
      return preparedPhoto;
   }
}
