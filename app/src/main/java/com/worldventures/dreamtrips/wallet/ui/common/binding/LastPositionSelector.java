package com.worldventures.dreamtrips.wallet.ui.common.binding;

import android.view.View;
import android.widget.EditText;

public class LastPositionSelector implements View.OnFocusChangeListener {

   @Override
   public void onFocusChange(View editTextView, boolean hasFocus) {
      if (hasFocus) {
         EditText editText = (EditText) editTextView;
         editText.setSelection(editText.length());
      }
   }
}
