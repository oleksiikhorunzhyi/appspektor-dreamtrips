package com.messenger.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class SelectionListenerEditText extends EditText {

   public interface SelectionListener {
      void onSelectionChange(int selStart, int selEnd);
   }

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
}
