package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FlagView extends FrameLayout {

   @InjectView(R.id.iv_flag) ImageView ivFlag;
   @InjectView(R.id.progress_flag) ProgressBar progressBar;

   public FlagView(Context context) {
      this(context, null);
   }

   public FlagView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public FlagView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      LayoutInflater.from(getContext()).inflate(R.layout.layout_flag_item, this, true);
      ButterKnife.inject(this);

      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlagView);
      Drawable drawable = a.getDrawable(R.styleable.FlagView_icon);
      if (drawable != null) ivFlag.setImageDrawable(drawable);
      a.recycle();
   }

   public void showProgress() {
      progressBar.setVisibility(View.VISIBLE);
   }

   public void hideProgress() {
      progressBar.setVisibility(View.GONE);
   }

   public void showFlagsPopup(List<Flag> flags, FlagPopupMenu.DialogConfirmationCallback dialogConfirmationCallback) {
      FlagPopupMenu popupMenu = new FlagPopupMenu(getContext(), this);
      popupMenu.show(flags, dialogConfirmationCallback);
   }
}
