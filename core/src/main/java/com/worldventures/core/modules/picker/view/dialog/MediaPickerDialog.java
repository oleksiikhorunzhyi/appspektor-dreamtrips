package com.worldventures.core.modules.picker.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.worldventures.core.R;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.modules.picker.model.MediaPickerAttachment;
import com.worldventures.core.modules.picker.presenter.dialog.MediaPickerDialogPresenter;
import com.worldventures.core.modules.picker.util.MediaPickerStep;
import com.worldventures.core.modules.picker.util.strategy.AdjustablePhotoPickStrategy;
import com.worldventures.core.modules.picker.util.strategy.AdjustableVideoPickStrategy;
import com.worldventures.core.modules.picker.util.strategy.DefaultPhotoStaticItemsStrategy;
import com.worldventures.core.modules.picker.util.strategy.MediaPickerStaticItemsStrategy;
import com.worldventures.core.modules.picker.util.strategy.PhotoPickLimitStrategy;
import com.worldventures.core.modules.picker.util.strategy.SimpleStaticItemsStrategy;
import com.worldventures.core.modules.picker.util.strategy.SinglePhotoPickStrategy;
import com.worldventures.core.modules.picker.util.strategy.SingleVideoLimitedDurationPickStrategy;
import com.worldventures.core.modules.picker.util.strategy.VideoPickLimitStrategy;
import com.worldventures.core.modules.picker.view.base.BaseMediaPickerLayout;
import com.worldventures.core.modules.picker.view.custom.MediaPickerContainer;
import com.worldventures.core.modules.picker.view.facebook.albums.FacebookAlbumsPickerLayout;
import com.worldventures.core.modules.picker.view.facebook.photos.FacebookPhotosPickerLayout;
import com.worldventures.core.modules.picker.view.gallery.GalleryMediaPickerLayout;
import com.worldventures.core.modules.picker.viewmodel.BaseMediaPickerViewModel;

import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import dagger.ObjectGraph;
import rx.Observable;

public class MediaPickerDialog extends BottomSheetDialog implements MediaPickerDialogView {

   private TextView selectedCount;
   private MediaPickerContainer mediaPickerContainer;
   private ViewFlipper pickerNavigationViewFlipper;

   @Inject MediaPickerDialogPresenter presenter;

   private final View contentView;
   private final ObjectGraph objectGraph;
   private final int requestId;

   private BottomSheetBehavior<View> bottomSheetBehavior;
   private OnDoneListener onDoneListener;
   private MediaPickerStaticItemsStrategy mediaPickerStaticItemsStrategy;
   private PhotoPickLimitStrategy photoPickLimitStrategy;
   private VideoPickLimitStrategy videoPickLimitStrategy;


   public MediaPickerDialog(@NonNull Context context) {
      this(context, 0, -1);
   }

   public MediaPickerDialog(@NonNull Context context, int requestId) {
      this(context, 0, requestId);
   }

   public MediaPickerDialog(@NonNull Context context, @StyleRes int theme, int requestId) {
      super(context, theme);
      //noinspection all
      this.objectGraph = (ObjectGraph) getContext().getSystemService(Injector.OBJECT_GRAPH_SERVICE_NAME);
      this.contentView = View.inflate(getContext(), R.layout.picker_dialog, null);
      this.requestId = requestId;
      setContentView(contentView);
      configureBottomSheetBehavior(contentView);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      objectGraph.inject(this);
      setOnShowListener(dialog -> onShow());
      setOnDismissListener(dialog -> onDismiss());
      setOnKeyListener((dialog, keyCode, event) -> presenter.handleKeyPress(keyCode, event));
   }

   private void onShow() {
      initUIWithListeners();
      mediaPickerContainer.setup(providePickerPages());
      mediaPickerContainer.setPickerOnPageChangedListener(this::flipNavigationButton);
      presenter.attachView(this);
   }

   private void onDismiss() {
      presenter.detachView(true);
      mediaPickerContainer.reset();
   }

   private void flipNavigationButton(boolean canGoBack) {
      final View viewFlipTo = pickerNavigationViewFlipper.findViewById(canGoBack ? R.id.btn_back : R.id.btn_cancel);
      final int viewIndexFlipTo = pickerNavigationViewFlipper.indexOfChild(viewFlipTo);
      if (viewIndexFlipTo != pickerNavigationViewFlipper.getDisplayedChild()) {
         pickerNavigationViewFlipper.setDisplayedChild(viewIndexFlipTo);
      }
   }

   @SuppressWarnings("ConstantConditions")
   private void initUIWithListeners() {
      selectedCount = findViewById(R.id.tv_selected_count);
      mediaPickerContainer = findViewById(R.id.picker_container);
      pickerNavigationViewFlipper = findViewById(R.id.flipper_picker_navigation);

      findViewById(R.id.btn_done).setOnClickListener(view -> onDone());
      findViewById(R.id.btn_cancel).setOnClickListener(view -> dismiss());
      findViewById(R.id.btn_back).setOnClickListener(view -> mediaPickerContainer.goBack());
   }

   private TreeMap<MediaPickerStep, BaseMediaPickerLayout> providePickerPages() {
      final TreeMap<MediaPickerStep, BaseMediaPickerLayout> pages = new TreeMap<>();

      final GalleryMediaPickerLayout gallery = new GalleryMediaPickerLayout(mediaPickerStaticItemsStrategy,
            photoPickLimitStrategy, videoPickLimitStrategy, getContext());
      gallery.setOnNextClickListener((args) -> mediaPickerContainer.goNext());
      objectGraph.inject(gallery);
      pages.put(gallery.getStep(), gallery);

      final FacebookAlbumsPickerLayout facebookAlbums = new FacebookAlbumsPickerLayout(getContext());
      facebookAlbums.setOnNextClickListener((args) -> mediaPickerContainer.goNext(args));
      facebookAlbums.setOnBackClickListener(() -> mediaPickerContainer.goBack());
      objectGraph.inject(facebookAlbums);
      pages.put(facebookAlbums.getStep(), facebookAlbums);

      final FacebookPhotosPickerLayout facebookPhotos = new FacebookPhotosPickerLayout(photoPickLimitStrategy,
            getContext());
      objectGraph.inject(facebookPhotos);
      pages.put(facebookPhotos.getStep(), facebookPhotos);
      return pages;
   }

   private void configureBottomSheetBehavior(View contentView) {
      this.bottomSheetBehavior = BottomSheetBehavior.from((View) contentView.getParent());

      if (bottomSheetBehavior != null) {
         bottomSheetBehavior.setHideable(true);
         bottomSheetBehavior.setPeekHeight(getContext().getResources()
               .getDimensionPixelSize(R.dimen.picker_panel_height));
      }
   }

   @Override
   public void onDone() {
      if (onDoneListener != null) {
         onDoneListener.onDone(presenter.providePickerResult());
      }
      dismiss();
   }

   @Override
   public void show() {
      this.mediaPickerStaticItemsStrategy = new SimpleStaticItemsStrategy();
      configureSinglePhotoPick();
      super.show();
   }

   public void show(String defaultPhotoUrl) {
      this.mediaPickerStaticItemsStrategy = new DefaultPhotoStaticItemsStrategy(defaultPhotoUrl);
      configureSinglePhotoPick();
      super.show();
   }

   public void show(int photoPickLimit) {
      configureMultiPhotoPick(photoPickLimit);
      this.videoPickLimitStrategy = new AdjustableVideoPickStrategy();
      super.show();
   }

   public void show(int photoPickLimit, int videoDurationLimit) {
      configureMultiPhotoPick(photoPickLimit);
      this.videoPickLimitStrategy = new SingleVideoLimitedDurationPickStrategy(videoDurationLimit);
      super.show();
   }

   public void show(int photoPickLimit, int videoPickLimit, int videoDurationLimit) {
      configureMultiPhotoPick(photoPickLimit);
      this.videoPickLimitStrategy = new AdjustableVideoPickStrategy(videoPickLimit, videoDurationLimit);
      super.show();
   }

   private void configureSinglePhotoPick() {
      this.photoPickLimitStrategy = new SinglePhotoPickStrategy();
      this.videoPickLimitStrategy = new AdjustableVideoPickStrategy();
   }

   private void configureMultiPhotoPick(int photoPickLimit) {
      this.mediaPickerStaticItemsStrategy = new SimpleStaticItemsStrategy();
      this.photoPickLimitStrategy = photoPickLimit != 0 ?
            new AdjustablePhotoPickStrategy(photoPickLimit) :
            new AdjustablePhotoPickStrategy();
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
      return mediaPickerContainer.canGoBack();
   }

   @Override
   public void goBack() {
      mediaPickerContainer.goBack();
   }

   @Override
   public Observable<List<BaseMediaPickerViewModel>> attachedMedia() {
      return Observable.<List<BaseMediaPickerViewModel>>merge(
            mediaPickerContainer.getScreens().get(MediaPickerStep.GALLERY).attachedItems(),
            mediaPickerContainer.getScreens().get(MediaPickerStep.FB_PHOTOS).attachedItems());
   }

   @Override
   public int getPickLimit() {
      return photoPickLimitStrategy.photoPickLimit();
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
      return RxLifecycleAndroid.bindView(contentView);
   }

   public interface OnDoneListener {
      void onDone(MediaPickerAttachment pickerAttachment);
   }
}
