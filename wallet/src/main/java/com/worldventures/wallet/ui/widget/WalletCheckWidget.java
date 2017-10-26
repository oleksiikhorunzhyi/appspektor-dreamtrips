package com.worldventures.wallet.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldventures.wallet.R;

public class WalletCheckWidget extends RelativeLayout {

   private final AppCompatDrawableManager appCompatDrawableManager = AppCompatDrawableManager.get();

   private final int checkedColor;
   private final int uncheckedColor;

   private final Drawable checkedStatusIcon;
   private final Drawable uncheckedStatusIcon;

   private final PorterDuffColorFilter checkedFilter;
   private final PorterDuffColorFilter uncheckedFilter;

   private ImageView ivStatusIcon;
   private ImageView ivIcon;
   private TextView tvTitle;
   private Drawable icon;
   private Boolean checked;

   public WalletCheckWidget(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public WalletCheckWidget(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      checkedColor = ContextCompat.getColor(context, R.color.wallet_success_green);
      uncheckedColor = ContextCompat.getColor(context, R.color.wallet_waring_red);
      checkedStatusIcon = appCompatDrawableManager.getDrawable(context, R.drawable.ic_wallet_check_success);
      uncheckedStatusIcon = appCompatDrawableManager.getDrawable(context, R.drawable.ic_wallet_vector_do_not_check);
      checkedFilter = AppCompatDrawableManager.getPorterDuffColorFilter(checkedColor, PorterDuff.Mode.MULTIPLY);
      uncheckedFilter = AppCompatDrawableManager.getPorterDuffColorFilter(uncheckedColor, PorterDuff.Mode.MULTIPLY);
      init(context, attrs);
   }

   private void init(Context context, @Nullable AttributeSet attrs) {
      final View view = LayoutInflater.from(getContext()).inflate(R.layout.wallet_custom_view_check, this);
      if (isInEditMode()) return;

      ivStatusIcon = view.findViewById(R.id.check_icon_status);
      ivIcon = view.findViewById(R.id.check_icon);
      tvTitle = view.findViewById(R.id.check_title);
      if (attrs != null) {
         TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.WalletCheckWidget);
         setChecked(arr.getBoolean(R.styleable.WalletCheckWidget_checked, false));
         setTitle(arr.getString(R.styleable.WalletCheckWidget_title));
         int iconResId = arr.getResourceId(R.styleable.WalletCheckWidget_icon, -1);
         if (iconResId != -1) {
            setIconDrawable(iconResId);
         }
         arr.recycle();
      }
      updateStatus();
   }

   public void setTitle(String title) {
      tvTitle.setText(title);
   }

   public void setTitle(@StringRes int title) {
      tvTitle.setText(title);
   }

   public void setIconDrawable(@DrawableRes int resId) {
      setIconDrawable(appCompatDrawableManager.getDrawable(getContext(), resId));
   }

   public void setIconDrawable(Drawable drawable) {
      PorterDuffColorFilter colorFiler = checked ? checkedFilter : uncheckedFilter;
      if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP && colorFiler == drawable.getColorFilter()) return;
      drawable.clearColorFilter();
      drawable.setColorFilter(colorFiler);
      ivIcon.setImageDrawable(drawable);
      icon = drawable;
   }

   public void setChecked(boolean checked) {
      if (this.checked != null && this.checked == checked) return;
      this.checked = checked;
      updateStatus();
   }

   private void updateStatus() {
      int color = checked ? checkedColor : uncheckedColor;
      tvTitle.setTextColor(color);
      ivStatusIcon.setImageDrawable(checked ? checkedStatusIcon : uncheckedStatusIcon);
      if (icon != null) {
         setIconDrawable(icon);
      }
   }
}
