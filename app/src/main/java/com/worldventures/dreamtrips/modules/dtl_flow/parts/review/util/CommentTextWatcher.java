package com.worldventures.dreamtrips.modules.dtl_flow.parts.review.util;

import android.text.Editable;
import android.widget.EditText;

import com.worldventures.dreamtrips.modules.common.view.util.TextWatcherAdapter;

import org.jetbrains.annotations.NotNull;

public class CommentTextWatcher extends TextWatcherAdapter {

   private final EditText editText;
   private final OnTextChangedListener onTextChangedListener;

   public CommentTextWatcher(@NotNull EditText editText, @NotNull OnTextChangedListener onTextChangedListener) {
      this.editText = editText;
      this.onTextChangedListener = onTextChangedListener;
      this.editText.addTextChangedListener(this);
   }

   @Override
   public void afterTextChanged(Editable s) {
      onTextChangedListener.onChange(s.toString());
   }

   public String getText() {
      return editText.getText().toString();
   }

   interface OnTextChangedListener {
      void onChange(String text);
   }
}
