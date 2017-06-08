package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.facebook.service.command.GetPhotosCommand;
import com.worldventures.dreamtrips.wallet.ui.common.picker.WalletPickLimitStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;
import com.worldventures.dreamtrips.wallet.ui.common.picker.dialog.WalletPickerStep;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.WalletPickerFacebookLayout;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletPickerFacebookPhotosLayout extends WalletPickerFacebookLayout<WalletPickerFacebookPhotosPresenter, WalletFacebookPhotoModel, WalletPickerFacebookPhotosAdapter> implements WalletPickerFacebookPhotosView {

   public static final String FB_ALBUM_ID = "fb_album_id";

   @Inject WalletPickerFacebookPhotosPresenter presenter;

   private final WalletPickLimitStrategy walletPickLimitStrategy;

   public WalletPickerFacebookPhotosLayout(WalletPickLimitStrategy walletPickLimitStrategy, @NonNull Context context) {
      this(walletPickLimitStrategy, context, null);
   }

   public WalletPickerFacebookPhotosLayout(WalletPickLimitStrategy walletPickLimitStrategy, @NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      this.walletPickLimitStrategy = walletPickLimitStrategy;
   }

   @Override
   public WalletPickerFacebookPhotosAdapter createAdapter(List<WalletFacebookPhotoModel> items, WalletPickerHolderFactory holderFactory) {
      return new WalletPickerFacebookPhotosAdapter(items, holderFactory);
   }

   @Override
   public void clearItems() {
      getAdapter().clear();
   }

   @Override
   public WalletPickerFacebookPhotosPresenter getPresenter() {
      return presenter;
   }

   @Override
   public WalletPickerStep getStep() {
      return WalletPickerStep.FB_PHOTOS;
   }

   @Override
   public String getFailedActionText() {
      return getContext().getString(R.string.wallet_picker_fb_photos_actions);
   }

   @Override
   public String getAlbumId() {
      if (getArguments() != null && getArguments().containsKey(FB_ALBUM_ID)) {
         return getArguments().getString(FB_ALBUM_ID);
      } else {
         return null;
      }
   }

   @Override
   public int getPickLimit() {
      return walletPickLimitStrategy.pickLimit();
   }

   @Override
   public List<WalletFacebookPhotoModel> getChosenPhotos() {
      return getAdapter().getChosenPhotos();
   }

   @Override
   public OperationView<GetPhotosCommand> provideOperationGetPhotos() {
      return new ComposableOperationView<>(this, this);
   }

   @Override
   public void handleItemClick(int position) {
      if (getAdapter().getItemViewType(position) == R.layout.adapter_item_photo_facebook) {
         updateItem(position);
         getPresenter().attachImages();
      }
   }

   private void updateItem(int position) {
      getAdapter().updateItem(position);
      boolean isLimitReached = isLimitReached(getChosenPhotos().size());
      if (isLimitReached) {
         if (walletPickLimitStrategy.pickLimit() > 1) {
            Toast.makeText(getContext(), getContext().getString(R.string.wallet_picker_limit_reached,
                  String.valueOf(walletPickLimitStrategy.pickLimit())), Toast.LENGTH_SHORT).show();
            getAdapter().updateItem(position);
         } else {
            WalletFacebookPhotoModel modelToRevert =
                  Queryable.from(getChosenPhotos())
                        .filter(element -> getAdapter().getPositionFromItem(element) != position).firstOrDefault();
            int modelToRevertPosition = getAdapter().getPositionFromItem(modelToRevert);
            getAdapter().updateItem(modelToRevertPosition);
         }
      }
   }

   private boolean isLimitReached(int count) {
      return walletPickLimitStrategy.pickLimit() > 0 && count > walletPickLimitStrategy.pickLimit();
   }
}
