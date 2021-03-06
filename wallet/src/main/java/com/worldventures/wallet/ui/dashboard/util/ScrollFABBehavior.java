package com.worldventures.wallet.ui.dashboard.util;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.wallet.R;

/**
 * Behavior is used in {@link R.layout.screen_wallet_cardlist}
 */
@SuppressWarnings("unused")
public class ScrollFABBehavior extends FloatingActionButton.Behavior {

   public ScrollFABBehavior(Context context, AttributeSet attrs) {
      super();
   }

   @Override
   public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout,
         FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
      return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
            || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
            nestedScrollAxes);
   }

   @Override
   public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
      super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

      if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
         //todo: this behavior was broken in 25.1.0 revision
         child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onHidden(FloatingActionButton fab) {
               child.setVisibility(View.INVISIBLE);
            }
         });
      } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
         child.show();
      }
   }

   @Override
   public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
      if (!child.isShown()) {
         View connectionPlank = findConnectionPlank(findRootView(parent));
         if (connectionPlank == null) {
            child.show();
         }
      }
      return super.onLayoutChild(parent, child, layoutDirection);
   }

   private ViewGroup findRootView(ViewGroup parent) {
      if (parent.getId() == R.id.screen_root) {
         return parent;
      } else {
         return findRootView((ViewGroup) parent.getParent());
      }
   }

   private View findConnectionPlank(ViewGroup parent) {
      for (int i = 0; i < parent.getChildCount(); i++) {
         View child = parent.getChildAt(i);
         if (child.getId() == R.id.plank_smartcard_connection) {
            return child;
         }
      }
      return null;
   }
}
