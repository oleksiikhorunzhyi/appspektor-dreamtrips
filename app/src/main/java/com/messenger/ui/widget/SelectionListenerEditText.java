package com.messenger.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class SelectionListenerEditText extends AppCompatEditText {

   private SelectionListener selectionListener;

   public SelectionListenerEditText(Context context) {
      super(context);
   }

   public SelectionListenerEditText(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public void setSelectionListener(SelectionListener selectionListener) {
      this.selectionListener = selectionListener;
   }

   @Override
   protected void onSelectionChanged(int selStart, int selEnd) {
      super.onSelectionChanged(selStart, selEnd);
      if (selectionListener != null) {
         selectionListener.onSelectionChange(selStart, selEnd);
      }
   }

   public interface SelectionListener {
      void onSelectionChange(int selStart, int selEnd);
   }
}
