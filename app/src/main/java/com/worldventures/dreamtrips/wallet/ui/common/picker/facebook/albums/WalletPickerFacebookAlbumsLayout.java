package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.facebook.exception.FacebookAccessTokenException;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetAlbumsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.picker.dialog.WalletPickerStep;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletPickerFacebookLayout;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos.WalletPickerFacebookPhotosLayout;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletPickerFacebookAlbumsLayout extends WalletPickerFacebookLayout<WalletPickerFacebookAlbumsPresenter, WalletFacebookAlbumModel> implements WalletPickerFacebookAlbumView {

   @Inject WalletPickerFacebookAlbumsPresenter presenter;

   public WalletPickerFacebookAlbumsLayout(@NonNull Context context) {
      super(context);
   }

   public WalletPickerFacebookAlbumsLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public void handleItemClick(int position) {
      if (getAdapter().getItemViewType(position) == R.layout.picker_adapter_item_album_facebook) {
         final WalletFacebookAlbumModel model = getAdapter().getItem(position);
         if (getOnNextClickListener() != null) {
            final Bundle args = new Bundle();
            args.putString(WalletPickerFacebookPhotosLayout.FB_ALBUM_ID, model.getId());
            getOnNextClickListener().onNextClick(args);
         }
      }
   }

   @Override
   public void clearItems() {
      getAdapter().clear();
   }

   @Override
   public WalletPickerFacebookAlbumsPresenter getPresenter() {
      return presenter;
   }

   @Override
   public WalletPickerStep getStep() {
      return WalletPickerStep.FB_ALBUMS;
   }

   @Override
   public String getFailedActionText() {
      return getContext().getString(R.string.wallet_picker_fb_albums_actions);
   }

   @Override
   public void showError(Object o, Throwable throwable) {
      final Throwable causeThrowable = throwable.getCause();
      if(!(causeThrowable instanceof FacebookAccessTokenException)) {
         super.showError(o, throwable);
      }
   }

   @Override
   public OperationView<GetAlbumsCommand> provideOperationGetAlbums() {
      return new ComposableOperationView<>(this, this);
   }

   @Override
   public void goBack() {
      if (getOnBackClickListener() != null) {
         getOnBackClickListener().onBackClick();
      }
   }
}
