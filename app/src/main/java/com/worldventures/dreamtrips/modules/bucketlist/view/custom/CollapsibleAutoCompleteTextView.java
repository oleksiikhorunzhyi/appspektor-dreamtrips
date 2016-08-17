package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.support.v7.view.CollapsibleActionView;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.techery.spares.utils.ui.SoftInputUtil;

public class CollapsibleAutoCompleteTextView extends AutoCompleteTextView implements CollapsibleActionView {

   public CollapsibleAutoCompleteTextView(Context context) {
      super(context);
   }

   public CollapsibleAutoCompleteTextView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public CollapsibleAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   @Override
   public void onActionViewExpanded() {
      SoftInputUtil.showSoftInputMethod(this);
   }

   @Override
   public void onActionViewCollapsed() {
      setText(null);
      SoftInputUtil.hideSoftInputMethod(this);
   }

   //fix bug on meizu
   @Override
   protected void replaceText(CharSequence text) {
      setText(null);
      super.replaceText(text);
   }

}
