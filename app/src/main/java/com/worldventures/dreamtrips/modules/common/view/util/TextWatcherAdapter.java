package com.worldventures.dreamtrips.modules.common.view.util;

import android.text.Editable;
import android.text.TextWatcher;

public class TextWatcherAdapter implements TextWatcher {
   @Override
   public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      //nothing to do here
   }

   @Override
   public void onTextChanged(CharSequence s, int start, int before, int count) {
      //nothing to do here
   }

   @Override
   public void afterTextChanged(Editable s) {
      //nothing to do here
   }
}
