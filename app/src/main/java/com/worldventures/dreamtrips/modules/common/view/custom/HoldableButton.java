package com.worldventures.dreamtrips.modules.common.view.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.worldventures.dreamtrips.R;

/**
 * This class represents Button with callbacks for holding
 * HoldableButton may be parametrized with next params:
 * - delay for start listening hold HoldableButton#holdInitialDelay
 * - delay between hold events HoldableButton#holdRepeatDelay
 */
public class HoldableButton extends Button {

   private static final int DEFAULT_HOLD_INITIAL_DELAY = 500;   //in milliseconds
   private static final int DEFAULT_HOLD_REPEAT_DELAY = 100;    //in milliseconds
   private int holdInitialDelay = DEFAULT_HOLD_INITIAL_DELAY;
   private int holdRepeatDelay = DEFAULT_HOLD_REPEAT_DELAY;

   private OnClickHoldListener onClickHoldListener;

   private boolean isHoldStarted;
   private int holdEventCount;

   private Runnable holdRunnable = new Runnable() {
      @Override
      public void run() {
         if (!isHoldStarted) {
            isHoldStarted = true;
            onHoldStart();
            holdEventCount = 0;
         }
         onHold();
         holdEventCount++;
         postDelayed(holdRunnable, holdRepeatDelay);
      }
   };

   public HoldableButton(Context context) {
      super(context);
   }

   public HoldableButton(Context context, AttributeSet attrs) {
      super(context, attrs);
      initializeWithAttrs(attrs);
   }

   public HoldableButton(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      initializeWithAttrs(attrs);
   }

   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
   public HoldableButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      initializeWithAttrs(attrs);
   }

   public void setOnClickHoldListener(OnClickHoldListener OnClickHoldListener) {
      this.onClickHoldListener = OnClickHoldListener;
   }

   public void setHoldInitialDelay(int holdInitialDelay) {
      this.holdInitialDelay = holdInitialDelay;
   }

   public void setHoldRepeatDelay(int holdRepeatDelay) {
      this.holdRepeatDelay = holdRepeatDelay;
   }

   private void initializeWithAttrs(AttributeSet attrs) {
      TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.HoldableButton);
      holdInitialDelay = arr.getInt(R.styleable.HoldableButton_holdInitialDelay, DEFAULT_HOLD_INITIAL_DELAY);
      holdRepeatDelay = arr.getInt(R.styleable.HoldableButton_holdRepeatDelay, DEFAULT_HOLD_REPEAT_DELAY);
      arr.recycle();
   }

   private void onClick() {
      performClick();
      if (onClickHoldListener != null) {
         onClickHoldListener.onClick(this);
      }
   }

   private void onHoldStart() {
      if (onClickHoldListener != null) {
         onClickHoldListener.onHoldStart(this);
      }
   }

   private void onHold() {
      if (onClickHoldListener != null) {
         onClickHoldListener.onHold(this);
      }
   }

   private void onHoldFinish(int holdEventCount) {
      if (onClickHoldListener != null) {
         onClickHoldListener.onHoldEnd(this, holdEventCount);
      }
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      int action = event.getAction();
      switch (action) {
         case MotionEvent.ACTION_DOWN:
            setPressed(true);
            postDelayed(holdRunnable, holdInitialDelay);
            return true;
         case MotionEvent.ACTION_UP:
            setPressed(false);
            if (!isHoldStarted) {
               onClick();
            } else {
               onHoldFinish(holdEventCount);
            }
            removeCallbacks(holdRunnable);
            isHoldStarted = false;
            return true;
         default:
            return super.onTouchEvent(event);
      }
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      removeCallbacks(holdRunnable);
   }

   public interface OnClickHoldListener {
      void onClick(View view);

      void onHoldStart(View view);

      void onHold(View view);

      void onHoldEnd(View view, int holdEventCount);
   }
}