package com.worldventures.dreamtrips.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.internal.Preconditions;

public class ViewUtils {

    private ViewUtils() {
    }

    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    public static int getMinSideSize(Activity activity) {
        return Math.min(getScreenHeight(activity), getScreenWidth(activity));
    }

    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        return height;
    }

    public static void removeSupportGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewTreeObserver.removeGlobalOnLayoutListener(listener);
        } else {
            viewTreeObserver.removeOnGlobalLayoutListener(listener);
        }
    }

    public static int getRootViewHeight(Activity activity) {
        int screenHeight = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
        int toolbarHeight = ((AppCompatActivity) activity).getSupportActionBar() != null ?
                ((AppCompatActivity) activity).getSupportActionBar().getHeight() : 0;
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
            boolean xlarge = (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
            boolean large = (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
            return xlarge || large;
        } else {
            return false;
        }
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
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
        boolean inHorizontalBounds = screenRect.left <= location[0] && screenRect.right >= location[0] + view.getWidth() && view.getWidth() > 0;
        boolean inVerticalBounds = screenRect.top <= location[1] && screenRect.bottom >= location[1] + view.getHeight() && view.getHeight() > 0;
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
        if (screenRect.left > viewRect.right || screenRect.right < viewRect.left || screenRect.top > viewRect.bottom || screenRect.bottom < viewRect.top) {
            return false;
        }
        return true;
    }

    public static void setViewVisibility(View view, int visibility) {
        Preconditions.checkNotNull(view, "view is null");
        if (view.getVisibility() != visibility) view.setVisibility(visibility);
    }

    public static void setViewVisibility(int visibility, View... views) {
        Queryable.from(views).forEachR(view -> setViewVisibility(view, visibility));
    }
}
