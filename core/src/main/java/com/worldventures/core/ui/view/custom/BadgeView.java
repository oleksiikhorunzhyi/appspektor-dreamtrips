package com.worldventures.core.ui.view.custom;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.worldventures.core.R;

/**
 * A simple text label view that can be applied as a "badge" to any given {@link android.view.View}.
 * This class is intended to be instantiated at runtime rather than included in XML layouts.
 *
 * @author Jeff Gilfelt
 */
public class BadgeView extends AppCompatTextView {

   private static final int DEFAULT_MARGIN_DIP = 5;
   private static final int DEFAULT_LR_PADDING_DIP = 5;
   private static final int DEFAULT_CORNER_RADIUS_DIP = 8;
   private static final int DEFAULT_BADGE_COLOR = Color.parseColor("#CCFF0000"); //Color.RED;
   private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

   private static Animation fadeIn;
   private static Animation fadeOut;

   private int badgePosition;
   private int badgeMarginH;
   private int badgeMarginV;
   private int badgeColor;

   private boolean isShown;

   private ShapeDrawable badgeBg;

   public BadgeView(Context context) {
      this(context, null);
   }

   public BadgeView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(context, attrs);
   }

   private void init(Context context, @Nullable AttributeSet attrs) {
      // apply defaults
      badgeMarginH = dipToPixels(DEFAULT_MARGIN_DIP);
      badgeMarginV = badgeMarginH;


      if (attrs != null) {
         TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.BadgeView);
         badgeColor = arr.getColor(R.styleable.BadgeView_background_color, DEFAULT_BADGE_COLOR);
         arr.recycle();
      } else {
         badgeColor = DEFAULT_BADGE_COLOR;
      }

      setTypeface(Typeface.DEFAULT_BOLD);
      int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
      setPadding(paddingPixels, 0, paddingPixels, 0);
      setTextColor(DEFAULT_TEXT_COLOR);

      fadeIn = new AlphaAnimation(0, 1);
      fadeIn.setInterpolator(new DecelerateInterpolator());
      fadeIn.setDuration(200);

      fadeOut = new AlphaAnimation(1, 0);
      fadeOut.setInterpolator(new AccelerateInterpolator());
      fadeOut.setDuration(200);

      isShown = false;

      show();

   }

   /**
    * Make the badge visible in the UI.
    */
   public void show() {
      show(false, null);
   }

   /**
    * Make the badge visible in the UI.
    *
    * @param animate flag to apply the default fade-in animation.
    */
   public void show(boolean animate) {
      show(animate, fadeIn);
   }

   /**
    * Make the badge visible in the UI.
    *
    * @param anim Animation to apply to the view when made visible.
    */
   public void show(Animation anim) {
      show(true, anim);
   }

   /**
    * Make the badge non-visible in the UI.
    */
   public void hide() {
      hide(false, null);
   }

   /**
    * Make the badge non-visible in the UI.
    *
    * @param animate flag to apply the default fade-out animation.
    */
   public void hide(boolean animate) {
      hide(animate, fadeOut);
   }

   /**
    * Make the badge non-visible in the UI.
    *
    * @param anim Animation to apply to the view when made non-visible.
    */
   public void hide(Animation anim) {
      hide(true, anim);
   }

   /**
    * Toggle the badge visibility in the UI.
    */
   public void toggle() {
      toggle(false, null, null);
   }

   /**
    * Toggle the badge visibility in the UI.
    *
    * @param animate flag to apply the default fade-in/out animation.
    */
   public void toggle(boolean animate) {
      toggle(animate, fadeIn, fadeOut);
   }

   /**
    * Toggle the badge visibility in the UI.
    *
    * @param animIn  Animation to apply to the view when made visible.
    * @param animOut Animation to apply to the view when made non-visible.
    */
   public void toggle(Animation animIn, Animation animOut) {
      toggle(true, animIn, animOut);
   }

   private void show(boolean animate, Animation anim) {
      if (getBackground() == null) {
         if (badgeBg == null) {
            badgeBg = getDefaultBackground();
         }
         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //noinspection all
            setBackgroundDrawable(badgeBg);
         } else {
            //noinspection all
            setBackground(badgeBg);
         }
      }

      if (animate) {
         this.startAnimation(anim);
      }
      this.setVisibility(View.VISIBLE);
      isShown = true;
   }

   private void hide(boolean animate, Animation anim) {
      this.setVisibility(View.GONE);
      if (animate) {
         this.startAnimation(anim);
      }
      isShown = false;
   }

   private void toggle(boolean animate, Animation animIn, Animation animOut) {
      if (isShown) {
         hide(animate && (animOut != null), animOut);
      } else {
         show(animate && (animIn != null), animIn);
      }
   }

   /**
    * Increment the numeric badge label. If the current badge label cannot be converted to
    * an integer value, its label will be set to "0".
    *
    * @param offset the increment offset.
    */
   public int increment(int offset) {
      CharSequence txt = getText();
      int i;
      if (txt != null) {
         try {
            i = Integer.parseInt(txt.toString());
         } catch (NumberFormatException e) {
            i = 0;
         }
      } else {
         i = 0;
      }
      i = i + offset;
      setText(String.valueOf(i));
      return i;
   }

   /**
    * Decrement the numeric badge label. If the current badge label cannot be converted to
    * an integer value, its label will be set to "0".
    *
    * @param offset the decrement offset.
    */
   public int decrement(int offset) {
      return increment(-offset);
   }

   private ShapeDrawable getDefaultBackground() {

      int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
      float[] outerR = new float[]{r, r, r, r, r, r, r, r};

      RoundRectShape rr = new RoundRectShape(outerR, null, null);
      ShapeDrawable drawable = new ShapeDrawable(rr);
      drawable.getPaint().setColor(badgeColor);

      return drawable;

   }

   /**
    * Is this badge currently visible in the UI?
    */
   @Override
   public boolean isShown() {
      return isShown;
   }

   /**
    * Returns the positioning of this badge.
    * <p>
    * one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT, POSTION_CENTER.
    */
   public int getBadgePosition() {
      return badgePosition;
   }

   /**
    * Set the positioning of this badge.
    *
    * @param layoutPosition one of POSITION_TOP_LEFT, POSITION_TOP_RIGHT, POSITION_BOTTOM_LEFT, POSITION_BOTTOM_RIGHT, POSTION_CENTER.
    */
   public void setBadgePosition(int layoutPosition) {
      this.badgePosition = layoutPosition;
   }

   /**
    * Returns the horizontal margin from the target View that is applied to this badge.
    */
   public int getHorizontalBadgeMargin() {
      return badgeMarginH;
   }

   /**
    * Returns the vertical margin from the target View that is applied to this badge.
    */
   public int getVerticalBadgeMargin() {
      return badgeMarginV;
   }

   /**
    * Set the horizontal/vertical margin from the target View that is applied to this badge.
    *
    * @param badgeMargin the margin in pixels.
    */
   public void setBadgeMargin(int badgeMargin) {
      this.badgeMarginH = badgeMargin;
      this.badgeMarginV = badgeMargin;
   }

   /**
    * Set the horizontal/vertical margin from the target View that is applied to this badge.
    *
    * @param horizontal margin in pixels.
    * @param vertical   margin in pixels.
    */
   public void setBadgeMargin(int horizontal, int vertical) {
      this.badgeMarginH = horizontal;
      this.badgeMarginV = vertical;
   }

   /**
    * Returns the color value of the badge background.
    */
   public int getBadgeBackgroundColor() {
      return badgeColor;
   }

   /**
    * Set the color value of the badge background.
    *
    * @param badgeColor the badge background color.
    */
   public void setBadgeBackgroundColor(int badgeColor) {
      this.badgeColor = badgeColor;
      badgeBg = getDefaultBackground();
   }

   private int dipToPixels(int dip) {
      Resources r = getResources();
      return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
   }
}
