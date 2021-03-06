package com.worldventures.dreamtrips.social.ui.bucketlist.view.custom;

import android.content.Context;
import android.support.v7.view.CollapsibleActionView;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;

import com.worldventures.core.ui.util.SoftInputUtil;

public class CollapsibleAutoCompleteTextView extends AppCompatAutoCompleteTextView implements CollapsibleActionView {

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
