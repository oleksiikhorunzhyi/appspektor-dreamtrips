package com.worldventures.dreamtrips.wallet.ui.common.picker.gallery;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Toast;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.command.GetPhotosFromGalleryCommand;
import com.worldventures.dreamtrips.wallet.ui.common.picker.WalletPickLimitStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.picker.WalletStaticItemsStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerLayout;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.WalletPickerHolderFactory;
import com.worldventures.dreamtrips.wallet.ui.common.picker.dialog.WalletPickerStep;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

public class WalletGalleryPickerLayout extends BaseWalletPickerLayout<WalletGalleryPickerPresenter, WalletGalleryPickerModel, WalletPickerGalleryAdapter> implements WalletGalleryPickerView {

   @Inject WalletGalleryPickerPresenter presenter;

   private final WalletStaticItemsStrategy walletStaticItemsStrategy;
   private final WalletPickLimitStrategy walletPickLimitStrategy;

   public WalletGalleryPickerLayout(WalletStaticItemsStrategy walletStaticItemsStrategy,
         WalletPickLimitStrategy pickLimitStrategy, @NonNull Context context) {
      this(walletStaticItemsStrategy, pickLimitStrategy, context, null);
   }

   public WalletGalleryPickerLayout(WalletStaticItemsStrategy walletStaticItemsStrategy,
         WalletPickLimitStrategy pickLimitStrategy, @NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      this.walletStaticItemsStrategy = walletStaticItemsStrategy;
      this.walletPickLimitStrategy = pickLimitStrategy;
   }

   @Override
   public WalletPickerGalleryAdapter createAdapter(List<WalletGalleryPickerModel> items, WalletPickerHolderFactory holderFactory) {
      return new WalletPickerGalleryAdapter(items, holderFactory);
   }

   @Override
   public void handleItemClick(int position) {
      if (getAdapter().getItemViewType(position) == R.layout.adapter_item_photo_pick) {
         updateItem(position);
         presenter.attachImages();
      } else if (getAdapter().getItemViewType(position) == R.layout.adapter_item_attach_photo) {
         handleAlternateSourcesClick(position);
      }
   }

   private void handleAlternateSourcesClick(int position) {
      final WalletIrregularPhotoModel item = (WalletIrregularPhotoModel) getAdapter().getItem(position);
      if (item.getType() == WalletIrregularPhotoModel.CAMERA) {
         presenter.tryOpenCamera();
      } else if (item.getType() == WalletIrregularPhotoModel.FACEBOOK) {
         if (getOnNextClickListener() != null) {
            getOnNextClickListener().onNextClick(null);
         }
      }
   }

   private boolean isLimitReached(int count) {
      return walletPickLimitStrategy.pickLimit() > 0 && count > walletPickLimitStrategy.pickLimit();
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
            WalletGalleryPickerModel modelToRevert =
                  Queryable.from(getChosenPhotos())
                        .filter(element -> getAdapter().getPositionFromItem(element) != position).firstOrDefault();
            int modelToRevertPosition = getAdapter().getPositionFromItem(modelToRevert);
            getAdapter().updateItem(modelToRevertPosition);
         }
      }
   }

   private int getAdapterOffset() {
      return walletStaticItemsStrategy.isExtraItemAvailable()
            ? walletStaticItemsStrategy.provideStaticItems().size() - 1
            : walletStaticItemsStrategy.provideStaticItems().size();
   }

   @Override
   public WalletGalleryPickerPresenter getPresenter() {
      return presenter;
   }

   @Override
   public WalletPickerStep getStep() {
      return WalletPickerStep.GALLERY;
   }

   @Override
   public String getFailedActionText() {
      return getContext().getString(R.string.wallet_picker_gallery_action);
   }

   @Override
   public void cameraPermissionGranted() {
      presenter.openCamera();
   }

   @Override
   public void showRationaleForCamera() {
      Toast.makeText(getContext(), R.string.permission_camera_rationale, Toast.LENGTH_SHORT).show();
   }

   @Override
   public void showDeniedForCamera() {
      Toast.makeText(getContext(), R.string.no_camera_permission, Toast.LENGTH_SHORT).show();
   }

   @Override
   public List<WalletGalleryPickerModel> provideStaticItems() {
      return walletStaticItemsStrategy.provideStaticItems();
   }

   @Override
   public OperationView<GetPhotosFromGalleryCommand> provideGalleryOperationView() {
      return new ComposableOperationView<>(this, this);
   }

   @Override
   public List<WalletGalleryPickerModel> getChosenPhotos() {
      return getAdapter().getChosenPhotos(getAdapterOffset());
   }

   @Override
   public void clearItems() {
      getAdapter().clear();
   }
}
