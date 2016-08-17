package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.State;

public class DtlFilterButton extends FrameLayout {

   @ColorRes private static final int DEF_ENABLED_BACKGROUND_COLOR_RES = R.color.dtlfb_enabled_background_color;
   @ColorRes private static final int DEF_DISABLED_BACKGROUND_COLOR_RES = R.color.dtlfb_disabled_background_color;
   @ColorRes private static final int DEF_ENABLED_CAPTION_COLOR_RES = R.color.dtlfb_enabled_caption_color;
   @ColorRes private static final int DEF_DISABLED_CAPTION_COLOR_RES = R.color.dtlfb_disabled_caption_color;
   @DrawableRes private static final int DEF_ENABLED_ICON_DRAWABLE_RES = R.drawable.ic_dtl_filter_button_enabled;
   @DrawableRes private static final int DEF_DISABLED_ICON_DRAWABLE_RES = R.drawable.ic_dtl_filter_button_disabled;
   private static final boolean DEF_SHOW_SEPARATOR = true;

   @InjectView(R.id.dtlfb_rootView) ViewGroup rootView;
   @InjectView(R.id.dtlfb_caption) AppCompatTextView caption;
   @InjectView(R.id.dtlfb_separator) View separator;

   @State protected boolean filterEnabled = false;

   private int enabledBackgroundColor;
   private int disabledBackgroundColor;
   private int enabledCaptionColor;
   private int disabledCaptionColor;
   @DrawableRes private int enabledIconDrawableRes;
   @DrawableRes private int disabledIconDrawableRes;
   private boolean showSeparator;

   public DtlFilterButton(Context context, AttributeSet attrs) {
      super(context, attrs);
      setId(R.id.dtlfb_rootView);
      inflate(getContext(), R.layout.view_dtl_filter_button_content, this);
      setLayoutTransition(new LayoutTransition()); // animateLayoutChanges="true"
      initAttributes(context, attrs);
   }

   private void initAttributes(Context context, AttributeSet attrs) {
      TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DtlFilterButton);
      showSeparator = a.getBoolean(R.styleable.DtlFilterButton_dtlfb_show_separator, DEF_SHOW_SEPARATOR);
      enabledBackgroundColor = ContextCompat.getColor(context, a.getResourceId(R.styleable.DtlFilterButton_dtlfb_enabled_background_color, DEF_ENABLED_BACKGROUND_COLOR_RES));
      disabledBackgroundColor = ContextCompat.getColor(context, a.getResourceId(R.styleable.DtlFilterButton_dtlfb_disabled_background_color, DEF_DISABLED_BACKGROUND_COLOR_RES));
      enabledCaptionColor = ContextCompat.getColor(context, a.getResourceId(R.styleable.DtlFilterButton_dtlfb_enabled_caption_color, DEF_ENABLED_CAPTION_COLOR_RES));
      disabledCaptionColor = ContextCompat.getColor(context, a.getResourceId(R.styleable.DtlFilterButton_dtlfb_disabled_caption_color, DEF_DISABLED_CAPTION_COLOR_RES));
      enabledIconDrawableRes = a.getResourceId(R.styleable.DtlFilterButton_dtlfb_enabled_icon, DEF_ENABLED_ICON_DRAWABLE_RES);
      disabledIconDrawableRes = a.getResourceId(R.styleable.DtlFilterButton_dtlfb_disabled_icon, DEF_DISABLED_ICON_DRAWABLE_RES);
      a.recycle();
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      ButterKnife.inject(this);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      redrawState();
   }

   protected void redrawState() {
      if (filterEnabled) {
         rootView.setBackgroundColor(enabledBackgroundColor);
         caption.setTextColor(enabledCaptionColor);
         caption.setCompoundDrawablesRelativeWithIntrinsicBounds(enabledIconDrawableRes, 0, 0, 0);
      } else {
         rootView.setBackgroundColor(disabledBackgroundColor);
         caption.setTextColor(disabledCaptionColor);
         caption.setCompoundDrawablesRelativeWithIntrinsicBounds(disabledIconDrawableRes, 0, 0, 0);
      }
      separator.setVisibility(!filterEnabled && showSeparator ? VISIBLE : GONE);
   }

   public void setFilterEnabled(final boolean enabled) {
      filterEnabled = enabled;
      redrawState();
   }

   ///////////////////////////////////////////////////////////////////////////
   // State saving
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public Parcelable onSaveInstanceState() {
      return Icepick.saveInstanceState(this, super.onSaveInstanceState());
   }

   @Override
   public void onRestoreInstanceState(Parcelable state) {
      super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
   }
}
