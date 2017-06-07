package com.worldventures.dreamtrips.modules.dtl_flow.parts.merchants;

import android.content.Context;
import android.widget.RatingBar;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.reviews.ReviewSummary;

public class ValidateReviewUtil {

   private static final float DEFAULT_RATING_VALUE = 0f;

   public static void setUpRating(Context context, ReviewSummary reviewSummary, RatingBar ratingBar, TextView ratingText) {
      if (reviewSummary == null || ratingBar == null) {
         return;
      }

      int total = parseTotal(reviewSummary.total());

      if (total == 0) {
         beTheFirstOne(context, ratingBar, ratingText);
         return;
      }

      ratingBar.setRating(parseAverage(reviewSummary.ratingAverage()));
      ratingText.setText(ViewUtils.getLabelReviews(context, total));
   }

   private static int parseTotal(String value) {
      int total = 0;
      try {
         total = Integer.parseInt(value);
      } catch (Exception e) {
         // nothing to do
      }
      return total;
   }

   private static float parseAverage(String value) {
      float average = 0f;
      try {
         average = Float.parseFloat(value);
      } catch (Exception e) {
         // Nothing to do
      }
      return average;
   }

   private static void beTheFirstOne(Context context, RatingBar ratingBar, TextView ratingText) {
      ratingBar.setRating(DEFAULT_RATING_VALUE);
      ratingText.setText(context.getString(R.string.to_be_first_reviews));
   }
}
