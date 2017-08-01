package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.modules.feed.model.PickerIrregularPhotoModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoStripButtonCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoStripPhotoCell;
import com.worldventures.dreamtrips.modules.feed.view.cell.PhotoStripVideoCell;
import com.worldventures.dreamtrips.modules.feed.view.util.PhotoStripItemDecorator;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.MediaPickerModelImpl;
import com.worldventures.dreamtrips.modules.media_picker.model.PhotoPickerModel;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoPickerModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Action2;

public class PhotoStripView extends LinearLayout {

   private static final long TRANSITION_ANIMATE_DURATION = 1000L;
   private static final long SHOWING_MESSAGE_DURATION = 3000L;

   private RecyclerView photosContainer;
   private TextView errorMessageView;
   private BaseDelegateAdapter mediaAdapter;

   private Injector injector;
   private EventListener eventListener;

   private int errorPlankHeight;

   public PhotoStripView(Context context) {
      super(context);
      init(null);
   }

   public PhotoStripView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public PhotoStripView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);
   }

   public void setEventListener(EventListener eventListener) {
      this.eventListener = eventListener;
   }

   public void setInjector(Injector injector) {
      this.injector = injector;
      initializeAdapter();
   }

   private void init(@Nullable AttributeSet attrs) {
      Context context = getContext();
      setOrientation(VERTICAL);
      setBackground(new ColorDrawable(getColor(android.R.color.transparent)));

      errorMessageView = new TextView(context);
      addView(errorMessageView);
      errorMessageView.setTypeface(null, Typeface.BOLD);
      errorMessageView.setGravity(Gravity.CENTER);
      errorMessageView.setVisibility(GONE);

      photosContainer = new RecyclerView(context);
      addView(photosContainer);
      photosContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
      photosContainer.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
      photosContainer.addItemDecoration(new PhotoStripItemDecorator());

      if (attrs != null) {
         TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PhotoStripView, 0, 0);
         try {
            errorPlankHeight = (int) a.getDimension(R.styleable.PhotoStripView_errorPlankHeight, context.getResources().getDimension(R.dimen.photo_strip_error_message_height));
            errorMessageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, errorPlankHeight));
            errorMessageView.setBackgroundColor(a.getColor(R.styleable.PhotoStripView_errorPlankColor, getColor(R.color.white)));
            errorMessageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.PhotoStripView_errorTextSize, 15));
            errorMessageView.setTextColor(a.getColor(R.styleable.PhotoStripView_errorTextColor, getColor(R.color.black)));

            photosContainer.setBackgroundColor(a.getColor(R.styleable.PhotoStripView_stripColor, getColor(R.color.white)));
         } finally {
            a.recycle();
         }
      }
   }

   private void initializeAdapter() {
      mediaAdapter = new BaseDelegateAdapter(getContext(), injector);
      mediaAdapter.registerCell(PhotoPickerModel.class, PhotoStripPhotoCell.class);
      mediaAdapter.registerDelegate(PhotoPickerModel.class, model -> {
         if(eventListener != null) eventListener.photoPickStatusChanged(((PhotoPickerModel) model).copy());
      });

      mediaAdapter.registerCell(VideoPickerModel.class, PhotoStripVideoCell.class);
      mediaAdapter.registerDelegate(VideoPickerModel.class, model -> {
         if(eventListener != null) eventListener.videoPickStatusChanged(((VideoPickerModel) model).copy());
      });

      mediaAdapter.registerCell(PickerIrregularPhotoModel.class, PhotoStripButtonCell.class);
      mediaAdapter.registerDelegate(PickerIrregularPhotoModel.class,  model -> {
         if(eventListener == null) return;
         if( ((PickerIrregularPhotoModel)model).getType() == PickerIrregularPhotoModel.CAMERA ) {
            eventListener.openCameraRequired();
         } else {
            eventListener.openPhotoPickerRequired();
         }
      });

      photosContainer.setAdapter(mediaAdapter);
   }

   private int getColor(@ColorRes int colorId){
      if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
         return getResources().getColor(colorId, null);
      } else{
         return getResources().getColor(colorId);
      }
   }

   public void showMedia(Collection<MediaPickerModel> photos) {
      if (mediaAdapter == null)
         throw new RuntimeException("You should provide injector first");

      List items = new ArrayList(photos);
      PickerIrregularPhotoModel cameraItem = new PickerIrregularPhotoModel(PickerIrregularPhotoModel.CAMERA,
            R.drawable.ic_picker_camera, R.string.camera, R.color.share_camera_color);
      PickerIrregularPhotoModel libraryItem = new PickerIrregularPhotoModel(PickerIrregularPhotoModel.LIBRARY,
            R.drawable.ic_photo_strip_library, R.string.library, R.color.share_camera_color);
      items.add(0, cameraItem);
      items.add(libraryItem);

      mediaAdapter.setItems(items);
   }

   public void updateMediaModel(MediaPickerModel updatedModel) {
      MediaPickerModelImpl updatedItem = (MediaPickerModelImpl) updatedModel;

      for (Object item: mediaAdapter.getItems()) {
         if (! (item instanceof MediaPickerModelImpl)) continue;

         MediaPickerModelImpl notUpdatedModel = (MediaPickerModelImpl) item;
         if (updatedItem.getFileName().equals(notUpdatedModel.getFileName())) {
            notUpdatedModel.setChecked(updatedModel.isChecked());
            mediaAdapter.updateItem(notUpdatedModel);
         }
      }
   }

   public void showError(@StringRes int message, Object... params) {
      errorMessageView.setText(getContext().getString(message, params));

      ObjectAnimator animatorEnter = ObjectAnimator.ofFloat(errorMessageView, View.Y, errorPlankHeight, 0)
            .setDuration(TRANSITION_ANIMATE_DURATION);
      ObjectAnimator animatorExit = ObjectAnimator.ofFloat(errorMessageView, View.Y, 0, errorPlankHeight)
            .setDuration(TRANSITION_ANIMATE_DURATION);
      animatorExit.setStartDelay(TRANSITION_ANIMATE_DURATION + SHOWING_MESSAGE_DURATION);

      AnimatorSet animation = new AnimatorSet();
      animation.playSequentially(animatorEnter, animatorExit);
      animation.addListener(new Animator.AnimatorListener() {
         @Override
         public void onAnimationStart(Animator animation) {
            errorMessageView.setVisibility(VISIBLE);
         }

         @Override
         public void onAnimationEnd(Animator animation) {
            errorMessageView.setVisibility(GONE);
         }

         @Override
         public void onAnimationCancel(Animator animation) {}

         @Override
         public void onAnimationRepeat(Animator animation) {}
      });
      animation.start();
   }

   public void showChooseCameraTypeDialog(Action1<MediaPickerModel.Type> cameraAction) {
      final String[] items = new String[]{getContext().getString(R.string.camera_take_a_picture), getContext().getString(R.string.camera_record_a_video)};
      new MaterialDialog.Builder(getContext())
            .items(items)
            .itemsCallback((dialog, itemView, which, text) -> {
               switch (which) {
                  case 0:
                     cameraAction.call(MediaPickerModel.Type.PHOTO);
                     break;
                  case 1:
                     cameraAction.call(MediaPickerModel.Type.VIDEO);
                     break;
               }
            }).show();
   }

   public void askUserForPermissions(String[] permissions, Action2<String[], Boolean> userAnswerListener) {
      if (permissions != PermissionConstants.STORE_PERMISSIONS) return;

      new MaterialDialog.Builder(getContext())
            .content(R.string.photo_strip_read_storage_permission_explanation)
            .positiveText(R.string.dialog_ok)
            .negativeText(R.string.dialog_cancel)
            .onPositive((materialDialog, dialogAction) -> userAnswerListener.call(permissions, true))
            .onNegative((materialDialog, dialogAction) -> userAnswerListener.call(permissions, false))
            .cancelable(false)
            .show();
   }

   public interface EventListener {

      void photoPickStatusChanged(PhotoPickerModel model);

      void videoPickStatusChanged(VideoPickerModel model);

      void openPhotoPickerRequired();

      void openCameraRequired();
   }

}

