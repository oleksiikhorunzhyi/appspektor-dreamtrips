package com.worldventures.dreamtrips.wallet.ui.wizard.pin.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

public class DirectionInfoView extends LinearLayout {

   private TextView tvTitle;
   private ImageView ivImage;

   public DirectionInfoView(Context context) {
      super(context);
      init(context, null);
   }

   public DirectionInfoView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(context, attrs);
   }

   public DirectionInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context, attrs);
   }

   private void init(Context context, AttributeSet attrs) {
      final View view = inflate(context, R.layout.wallet_custom_view_guesture_info, this);
      if (isInEditMode()) return;
      tvTitle = view.findViewById(R.id.tv_title);
      ivImage = view.findViewById(R.id.iv_image);
      if (attrs != null) {
         TypedArray a = context.getTheme().obtainStyledAttributes(
               attrs,
               R.styleable.DirectionInfoView,
               0, 0);
         try {
            setTitle(a.getString(R.styleable.DirectionInfoView_infoViewTitle));
            setImage(a.getResourceId(R.styleable.DirectionInfoView_infoViewImage, -1));
         } finally {
            a.recycle();
         }
      }
   }

   public void setTitle(String title) {
      tvTitle.setText(title);
   }

   public void setImage(int imageResId) {
      ivImage.setImageResource(imageResId);
   }
}
