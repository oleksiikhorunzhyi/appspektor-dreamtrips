package com.worldventures.dreamtrips.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.widget.TextView;

public class SpanUtils {

   public static void stripUnderlines(TextView textView) {
      Spannable s = (Spannable) textView.getText();
      URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
      for (URLSpan span : spans) {
         int start = s.getSpanStart(span);
         int end = s.getSpanEnd(span);
         s.removeSpan(span);
         span = new URLSpanNoUnderline(span.getURL());
         s.setSpan(span, start, end, 0);
      }
      textView.setText(s);
   }

   public static class URLSpanNoUnderline extends URLSpan {
      public URLSpanNoUnderline(String url) {
         super(url);
      }

      @Override
      public void updateDrawState(TextPaint ds) {
         super.updateDrawState(ds);
         ds.setUnderlineText(false);
      }
   }

   public static SpannableStringBuilder trimSpannable(SpannableStringBuilder spannable) {
      if (TextUtils.isEmpty(spannable)) return spannable;

      int trimStart = 0;
      int trimEnd = 0;

      String text = spannable.toString();

      while (text.length() > 0 && text.startsWith("\n")) {
         text = text.substring(1);
         trimStart += 1;
      }

      while (text.length() > 0 && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
         trimEnd += 1;
      }

      return spannable.delete(0, trimStart).delete(spannable.length() - trimEnd, spannable.length());
   }
}
