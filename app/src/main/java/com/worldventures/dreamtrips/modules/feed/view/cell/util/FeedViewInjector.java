package com.worldventures.dreamtrips.modules.feed.view.cell.util;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

public class FeedViewInjector {

   private Context context;

   public FeedViewInjector(Context context) {
      this.context = context;
   }

   public void initCardViewWrapper(CardView cardView) {
      if (ViewUtils.isTablet(context)) {
         cardView.setCardElevation(4);
         int m = context.getResources().getDimensionPixelSize(R.dimen.spacing_small);
         ((ViewGroup.MarginLayoutParams) cardView.getLayoutParams()).setMargins(m, m, m, m);
      } else {
         cardView.setCardElevation(0);
      }
   }
}
