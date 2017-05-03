package com.worldventures.dreamtrips.wallet.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

/**
 * Created by Dmitry Reutov on 12/16/16.
 */

public class WalletCardNameUtil {

   public static void bindSpannableStringToTarget(TextView targetTextView, int bodyStringResId, int postfixStrinResId, boolean isRequired, boolean asHint) {
      SpannableStringBuilder bodyStringBuilder = new SpannableStringBuilder()
            .append(targetTextView.getContext().getString(bodyStringResId));
      if (isRequired) {
         bodyStringBuilder.append(spannableRequiredFields());
      }
      if (postfixStrinResId != 0) {
            bodyStringBuilder.append(" ")
            .append(reduceSizeSpannable(targetTextView.getContext().getString(postfixStrinResId)));
      }
      setString(targetTextView, bodyStringBuilder, asHint);
   }

   public static void bindSpannableStringToTarget(TextView targetTextView, int bodyStringResId, boolean isRequired, boolean asHint) {
      bindSpannableStringToTarget(targetTextView, bodyStringResId, 0, isRequired, asHint);
   }

   public static SpannableString spannableRequiredFields() {
      final SpannableString requiredFields = new SpannableString("*");
      requiredFields.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      return requiredFields;
   }

   public static SpannableString reduceSizeSpannable(String text) {
      final SpannableString spannableString = new SpannableString(text);
      spannableString.setSpan(new RelativeSizeSpan(.75f), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      return spannableString;
   }

   private static void setString(TextView targetTextView, SpannableStringBuilder stringBuilder, boolean asHint) {
      if (asHint) {
         targetTextView.setHint(stringBuilder);
      } else {
         targetTextView.setText(stringBuilder);
      }
   }
}
