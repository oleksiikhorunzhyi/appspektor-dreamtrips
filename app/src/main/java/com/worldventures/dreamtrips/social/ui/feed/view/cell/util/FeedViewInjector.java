package com.worldventures.dreamtrips.social.ui.feed.view.cell.util;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;

public class FeedViewInjector {

   private Context context;

   public FeedViewInjector(Context context) {
      this.context = context;
   }

   public void initCardViewWrapper(CardView cardView) {
      if (ViewUtils.isTablet(context)) {
         if (((int) cardView.getCardElevation()) != 4) {
            cardView.setCardElevation(4);
            int m = context.getResources().getDimensionPixelSize(R.dimen.spacing_small);
            ((ViewGroup.MarginLayoutParams) cardView.getLayoutParams()).setMargins(m, m, m, m);
         }
      } else {
         if (((int) cardView.getCardElevation()) != 0) {
            cardView.setCardElevation(0);
         }
      }
   }
}
