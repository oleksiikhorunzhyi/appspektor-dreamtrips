package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ms.square.android.expandabletextview.ExpandableTextView;

/**
 * Edward on 03.02.15.
 * expandable text view which returns callbacks when pressed
 */
public class ExpandableTextViewCallable extends ExpandableTextView {

   private OnTouchedListener onTouchedListener;

   public ExpandableTextViewCallable(Context context) {
      super(context);
   }

   public ExpandableTextViewCallable(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public ExpandableTextViewCallable(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
   }

   public void setOnTouchedListener(OnTouchedListener onTouchedListener) {
      this.onTouchedListener = onTouchedListener;
   }

   public void setMovementMethod(MovementMethod movementMethod) {
      mTv.setMovementMethod(movementMethod);
   }

   @Override
   public boolean onInterceptTouchEvent(MotionEvent ev) {
      if (ev.getAction() == MotionEvent.ACTION_UP) {
         onTouchedListener.onTouched();
      }
      return super.onInterceptTouchEvent(ev);
   }

   public interface OnTouchedListener {
      void onTouched();
   }
}
