package com.worldventures.dreamtrips.wallet.ui.common.picker.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.module.Injector;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.MediaPickerAttachment;
import com.worldventures.dreamtrips.wallet.ui.common.picker.AdjustablePickStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.picker.DefaultPhotoStaticItemsStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.picker.SimpleWalletStaticItemsStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.picker.SinglePickStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.picker.WalletPickLimitStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.picker.WalletStaticItemsStrategy;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BasePickerViewModel;
import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerLayout;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.albums.WalletPickerFacebookAlbumsLayout;
import com.worldventures.dreamtrips.wallet.ui.common.picker.facebook.photos.WalletPickerFacebookPhotosLayout;
import com.worldventures.dreamtrips.wallet.ui.common.picker.gallery.WalletGalleryPickerLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;


public class WalletPickerDialog extends BottomSheetDialog implements WalletPickerDialogView {

   @InjectView(R.id.tv_selected_count) TextView selectedCount;
   @InjectView(R.id.wallet_picker_container) WalletPickerContainer walletPickerContainer;

   @Inject WalletPickerDialogPresenter presenter;

   private final View contentView;
   private final Injector injector;
   private final int requestId;

   private BottomSheetBehavior<View> bottomSheetBehavior;
   private OnDoneListener onDoneListener;
   private WalletStaticItemsStrategy walletStaticItemsStrategy;
   private WalletPickLimitStrategy walletPickLimitStrategy;


   public WalletPickerDialog(@NonNull Context context, Injector injector) {
      this(context, injector, 0, -1);
   }

   public WalletPickerDialog(@NonNull Context context, Injector injector, int requestId) {
      this(context, injector, 0, requestId);
   }

   public WalletPickerDialog(@NonNull Context context, Injector injector, @StyleRes int theme, int requestId) {
      super(context, theme);
      this.injector = injector;
      this.contentView = View.inflate(getContext(), R.layout.wallet_picker_dialog, null);
      this.requestId = requestId;
      setContentView(contentView);
      configureBottomSheetBehavior(contentView);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      injector.inject(this);
      setOnShowListener(dialog -> {
         ButterKnife.inject(this);
         walletPickerContainer.setup(providePickerPages());
         presenter.attachView(this);
      });
      setOnDismissListener(dialog -> {
         presenter.detachView(true);
         walletPickerContainer.reset();
         ButterKnife.reset(this);
      });
      setOnKeyListener((dialog, keyCode, event) -> presenter.handleKeyPress(keyCode, event));
   }

   private TreeMap<WalletPickerStep, BaseWalletPickerLayout> providePickerPages() {
      final TreeMap<WalletPickerStep, BaseWalletPickerLayout> pages = new TreeMap<>();
      final WalletGalleryPickerLayout gallery = new WalletGalleryPickerLayout(walletStaticItemsStrategy,
            walletPickLimitStrategy, getContext());
      gallery.setOnNextClickListener((args) -> walletPickerContainer.goNext());
      injector.inject(gallery);
      pages.put(gallery.getStep(), gallery);
      final WalletPickerFacebookAlbumsLayout facebookAlbums = new WalletPickerFacebookAlbumsLayout(getContext());
      facebookAlbums.setOnNextClickListener((args) -> walletPickerContainer.goNext(args));
      facebookAlbums.setOnBackClickListener(() -> walletPickerContainer.goBack());
      injector.inject(facebookAlbums);
      pages.put(facebookAlbums.getStep(), facebookAlbums);
      final WalletPickerFacebookPhotosLayout facebookPhotos = new WalletPickerFacebookPhotosLayout(walletPickLimitStrategy,
            getContext());
      injector.inject(facebookPhotos);
      pages.put(facebookPhotos.getStep(), facebookPhotos);
      return pages;
   }

   private void configureBottomSheetBehavior(View contentView) {
      this.bottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());

      if (bottomSheetBehavior != null) {
         bottomSheetBehavior.setHideable(true);
         bottomSheetBehavior.setPeekHeight(getContext().getResources().getDimensionPixelSize(R.dimen.picker_panel_height));
      }
   }

   @Override
   @OnClick(R.id.btn_done)
   public void onDone() {
      if (onDoneListener != null) {
         onDoneListener.onDone(presenter.providePickerResult());
      }
      dismiss();
   }

   @OnClick(R.id.btn_cancel)
   public void onCancel() {
      dismiss();
   }

   @Override
   public void show() {
      this.walletStaticItemsStrategy = new SimpleWalletStaticItemsStrategy();
      this.walletPickLimitStrategy = new SinglePickStrategy();
      super.show();
   }

   public void show(String defaultPhotoUrl) {
      this.walletStaticItemsStrategy = new DefaultPhotoStaticItemsStrategy(defaultPhotoUrl);
      this.walletPickLimitStrategy = new SinglePickStrategy();
      super.show();
   }

   public void show(int photoPickLimit) {
      show(photoPickLimit, 0);
   }

   public void show(int photoPickLimit, int videoPickLimit) {
      this.walletStaticItemsStrategy = new SimpleWalletStaticItemsStrategy();
      this.walletPickLimitStrategy = photoPickLimit != 0 ?
            new AdjustablePickStrategy(photoPickLimit, videoPickLimit) :
            new AdjustablePickStrategy();
      super.show();
   }


   @Override
   public void updatePickedItemsCount(int count) {
      if (count == 0) {
         selectedCount.setText("");
      } else {
         selectedCount.setText(getContext().getString(R.string.photos_selected, count));
      }
   }

   @Override
   public boolean canGoBack() {
      return walletPickerContainer.canGoBack();
   }

   @Override
   public void goBack() {
      walletPickerContainer.goBack();
   }

   @Override
   public Observable<List<BasePickerViewModel>> attachedMedia() {
      return Observable.<List<BasePickerViewModel>, List<BasePickerViewModel>, List<BasePickerViewModel>>combineLatest(
            walletPickerContainer.getScreens().get(WalletPickerStep.GALLERY).attachedItems(),
            walletPickerContainer.getScreens().get(WalletPickerStep.FB_PHOTOS).attachedItems(),
            (galleryAttachment, facebookAttachment) -> {
               final List<BasePickerViewModel> combinedAttachments = new ArrayList<>();
               combinedAttachments.addAll(galleryAttachment);
               combinedAttachments.addAll(facebookAttachment);
               return Collections.unmodifiableList(combinedAttachments);
            });
   }

   @Override
   public int getPickLimit() {
      return walletPickLimitStrategy.photoPickLimit();
   }

   @Override
   public int getRequestId() {
      return requestId;
   }

   public void setOnDoneListener(OnDoneListener onDoneListener) {
      this.onDoneListener = onDoneListener;
   }

   @Override
   public <T> Observable.Transformer<T, T> lifecycle() {
      return RxLifecycle.bindView(contentView);
   }

   public interface OnDoneListener {
      void onDone(MediaPickerAttachment pickerAttachment);
   }
}
