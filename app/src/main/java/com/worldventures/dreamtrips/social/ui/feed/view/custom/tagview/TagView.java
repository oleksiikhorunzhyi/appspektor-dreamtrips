package com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.worldventures.core.utils.Size;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.social.ui.feed.view.custom.tagview.viewgroup.newio.model.TagPosition;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import butterknife.InjectView;
import butterknife.Optional;

public abstract class TagView<T extends TagActionListener> extends RelativeLayout {

   protected PhotoTag photoTag;
   protected T tagListener;
   protected Photo photo;

   @Optional @InjectView(R.id.pointer_top) View pointerTop;
   @Optional @InjectView(R.id.pointer_bottom) View pointerBottom;
   @Optional @InjectView(R.id.pointer_shift_x) View space;

   TagPosition absoluteTagPosition;

   public TagView(Context context) {
      this(context, null);
   }

   public TagView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
      initialize();
   }

   public void setPhotoTag(PhotoTag photoTag) {
      this.photoTag = photoTag;
   }

   public void setPhoto(Photo photo) {
      this.photo = photo;
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      setupPointers();
   }

   protected void setupPointers() {
      int leftMargin = ((LayoutParams) this.getLayoutParams()).leftMargin;
      int topMargin = ((LayoutParams) this.getLayoutParams()).topMargin;
      float tagPosition = absoluteTagPosition.getTopLeft().getX();
      pointerTop.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
      int width = pointerTop.getMeasuredWidth();
      int pointerMargin = (int) (tagPosition - leftMargin - (width / 2));
      int absoluteWidth = (int) (absoluteTagPosition.getBottomRight().getX() - absoluteTagPosition.getTopLeft().getX());
      space.getLayoutParams().width = pointerMargin + absoluteWidth / 2;

      int y = (int) absoluteTagPosition.getTopLeft().getY();
      if (y > topMargin + this.getHeight()) {
         pointerTop.setVisibility(GONE);
         pointerBottom.setVisibility(VISIBLE);
      } else {
         pointerTop.setVisibility(VISIBLE);
         pointerBottom.setVisibility(GONE);
      }
   }

   public void setTagListener(T tagListener) {
      this.tagListener = tagListener;
   }

   public Size getSize() {
      measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
      int width = getMeasuredWidth();
      int height = getMeasuredHeight();
      return new Size(width, height);
   }

   protected abstract void initialize();

   protected void deleteTag() {
      ((ViewGroup) getParent()).removeView(this);
   }

   public TagPosition getAbsoluteTagPosition() {
      return absoluteTagPosition;
   }

   public void setAbsoluteTagPosition(TagPosition absoluteTagPosition) {
      this.absoluteTagPosition = absoluteTagPosition;
   }

   public PhotoTag getPhotoTag() {
      return photoTag;
   }
}