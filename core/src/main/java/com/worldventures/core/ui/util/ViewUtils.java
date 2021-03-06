package com.worldventures.core.ui.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.internal.Preconditions;

public final class ViewUtils {

   private ViewUtils() {
   }

   public static int getScreenWidth(Activity activity) {
      Display display = activity.getWindowManager().getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      return size.x;
   }

   public static int getMinSideSize(Activity activity) {
      return Math.min(getScreenHeight(activity), getScreenWidth(activity));
   }

   public static int getScreenHeight(Activity activity) {
      Display display = activity.getWindowManager().getDefaultDisplay();
      Point size = new Point();
      display.getSize(size);
      return size.y;
   }

   public static void removeSupportGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
      ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
         viewTreeObserver.removeGlobalOnLayoutListener(listener);
      } else {
         viewTreeObserver.removeOnGlobalLayoutListener(listener);
      }
   }

   public static void runTaskAfterMeasure(View view, Runnable task) {
      if (view.getHeight() > 0 && view.getWidth() > 0) {
         task.run();
         return;
      }

      view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
         @Override
         public boolean onPreDraw() {
            view.getViewTreeObserver().removeOnPreDrawListener(this);
            task.run();
            return true;
         }
      });
   }

   public static int getRootViewHeight(Activity activity) {
      int screenHeight = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
      int toolbarHeight = ((AppCompatActivity) activity).getSupportActionBar() != null ? ((AppCompatActivity) activity).getSupportActionBar()
            .getHeight() : 0;
      return screenHeight - toolbarHeight;
   }

   public static int getStatusBarHeight(Activity activity) {
      int result = 0;
      int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
      if (resourceId > 0) {
         result = activity.getResources().getDimensionPixelSize(resourceId);
      }
      return result;
   }

   public static boolean isLandscapeOrientation(Context context) {
      int orientation = context.getResources().getConfiguration().orientation;
      return Configuration.ORIENTATION_LANDSCAPE == orientation;

   }

   public static boolean isTablet(Context context) {
      if (context != null) {
         boolean xlarge = (context.getResources()
               .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
         boolean large = (context.getResources()
               .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
         return xlarge || large;
      } else {
         return false;
      }
   }

   public static boolean isTabletLandscape(Context context) {
      return ViewUtils.isTablet(context) && ViewUtils.isLandscapeOrientation(context);
   }

   public static boolean isPhoneLandscape(Context context) {
      return !ViewUtils.isTablet(context) && ViewUtils.isLandscapeOrientation(context);
   }

   public static float dpFromPx(final Resources resources, final float px) {
      return px / resources.getDisplayMetrics().density;
   }

   public static float pxFromDp(final Context context, final float dp) {
      return dp * context.getResources().getDisplayMetrics().density;
   }

   public static boolean isFullVisibleOnScreen(Fragment fragment) {
      return isFullVisibleOnScreen(fragment.getActivity(), fragment.getView());
   }

   public static boolean isFullVisibleOnScreen(Activity activity, View view) {
      if (activity == null || view == null) {
         return false; // ASK better throw exception?
      }
      Rect screenRect = new Rect();
      activity.getWindow().getDecorView().getGlobalVisibleRect(screenRect);
      int[] location = new int[2];
      view.getLocationOnScreen(location);
      boolean inHorizontalBounds = screenRect.left <= location[0] && screenRect.right >= location[0] + view.getWidth() && view
            .getWidth() > 0;
      boolean inVerticalBounds = screenRect.top <= location[1] && screenRect.bottom >= location[1] + view.getHeight() && view
            .getHeight() > 0;
      return view.isShown() && inHorizontalBounds && inVerticalBounds;
   }

   public static boolean isPartVisibleOnScreen(Fragment fragment) {
      return isPartVisibleOnScreen(fragment.getActivity(), fragment.getView());
   }

   public static boolean isPartVisibleOnScreen(Activity activity, View view) {
      if (activity == null || view == null) {
         return false; // ASK better throw exception?
      }
      Rect screenRect = new Rect();
      activity.getWindow().getDecorView().getGlobalVisibleRect(screenRect);
      int[] location = new int[2];
      view.getLocationOnScreen(location);

      if (view.getWidth() <= 0 || view.getHeight() <= 0) {
         return false;
      }

      Rect viewRect = new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight());
      return !(
            screenRect.left > viewRect.right
                  || screenRect.right < viewRect.left
                  || screenRect.top > viewRect.bottom
                  || screenRect.bottom < viewRect.top
      );
   }

   public static void setCompatDrawable(View view, @DrawableRes int resId) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { view.setBackgroundResource(resId); } else {
         view.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), resId));
      }
   }

   public static void setTextColor(@NonNull Button view, @ColorRes int color) {
      view.setTextColor(ContextCompat.getColor(view.getContext(), color));
   }

   public static void setTextOrHideView(TextView textView, CharSequence text) {
      if (!android.text.TextUtils.isEmpty(text)) {
         setViewVisibility(textView, View.VISIBLE);
         textView.setText(text);
      } else { setViewVisibility(textView, View.GONE); }
   }

   public static void setViewVisibility(View view, int visibility) {
      Preconditions.checkNotNull(view, "view is null");
      if (view.getVisibility() != visibility) { view.setVisibility(visibility); }
   }

   public static void setViewVisibility(int visibility, View... views) {
      Queryable.from(views).forEachR(view -> setViewVisibility(view, visibility));
   }

   public static String getLabelReviews(int totalReviews, String formatReviewSingle, String formatReviewMultiple) {
      return totalReviews == 1 ? String.format(formatReviewSingle, totalReviews) : String.format(formatReviewMultiple, totalReviews);
   }

   public static String getStringById(Context context, @StringRes int text) {
      return context.getResources().getString(text);
   }

   public static void setTextAppearance(Context context, TextView textView, @StyleRes int textAppearanceStyle) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         textView.setTextAppearance(textAppearanceStyle);
      } else {
         textView.setTextAppearance(context, textAppearanceStyle);
      }
   }
}
