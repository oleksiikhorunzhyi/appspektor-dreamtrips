package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BadgeImageView extends RelativeLayout {

   @InjectView(R.id.image) ImageView image;
   @InjectView(R.id.badge) BadgeView badge;

   public BadgeImageView(Context context) {
      this(context, null);
   }

   public BadgeImageView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public BadgeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      LayoutInflater.from(getContext()).inflate(R.layout.layout_badge_item, this, true);
      ButterKnife.inject(this);
   }

   public void setBadgeValue(int count) {
      if (count > 0) {
         badge.setVisibility(VISIBLE);
         badge.setText(String.valueOf(count));
      } else {
         badge.setVisibility(GONE);
      }
   }

   public void setImage(@DrawableRes int res) {
      image.setImageResource(res);
   }

}
