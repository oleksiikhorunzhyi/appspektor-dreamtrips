package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class KeyCallbackEditText extends AppCompatEditText {

   private OnKeyPreImeListener onKeyPreImeListener;

   public KeyCallbackEditText(Context context) {
      super(context);
   }

   public KeyCallbackEditText(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public KeyCallbackEditText(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @Override
   public boolean onKeyPreIme(int keyCode, KeyEvent event) {
      if (onKeyPreImeListener != null) {
         onKeyPreImeListener.onKeyPressed(keyCode, event);
      }
      return super.onKeyPreIme(keyCode, event);
   }

   public void setOnKeyPreImeListener(OnKeyPreImeListener onKeyPreImeListener) {
      this.onKeyPreImeListener = onKeyPreImeListener;
   }

   public interface OnKeyPreImeListener {
      void onKeyPressed(int keyCode, KeyEvent event);
   }
}
